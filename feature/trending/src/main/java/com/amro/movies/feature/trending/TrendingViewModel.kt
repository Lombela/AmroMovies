package com.amro.movies.feature.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.usecase.GetTrendingMoviesUseCase
import com.amro.movies.domain.usecase.RefreshTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val refreshTrendingMoviesUseCase: RefreshTrendingMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    private val userId = Constants.DEFAULT_USER_ID

    init {
        observeMovies()
        refreshMovies(showErrors = false)
    }

    fun onEvent(event: TrendingEvent) {
        when (event) {
            is TrendingEvent.LoadMovies -> refreshMovies(showErrors = false)
            is TrendingEvent.Retry -> refreshMovies(showErrors = true)
            is TrendingEvent.SelectGenre -> selectGenre(event.genre)
            is TrendingEvent.SetSortOption -> setSortOption(event.option)
            is TrendingEvent.SetSortOrder -> setSortOrder(event.order)
            is TrendingEvent.ShowFilterSheet -> _uiState.update { it.copy(isFilterSheetVisible = true) }
            is TrendingEvent.HideFilterSheet -> _uiState.update { it.copy(isFilterSheetVisible = false) }
            is TrendingEvent.ShowSortSheet -> _uiState.update { it.copy(isSortSheetVisible = true) }
            is TrendingEvent.HideSortSheet -> _uiState.update { it.copy(isSortSheetVisible = false) }
            is TrendingEvent.ClearFilters -> clearFilters()
        }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            getTrendingMoviesUseCase(userId).collect { movies ->
                _uiState.update { state ->
                    val availableGenres = extractGenresFromMovies(movies)
                    val filteredMovies = applyFiltersAndSort(movies, state)
                    state.copy(
                        isLoading = state.isLoading && movies.isEmpty(),
                        movies = movies,
                        filteredMovies = filteredMovies,
                        availableGenres = availableGenres,
                        error = if (movies.isNotEmpty()) null else state.error
                    )
                }
            }
        }
    }

    private fun refreshMovies(showErrors: Boolean) {
        viewModelScope.launch {
            val hasCache = _uiState.value.movies.isNotEmpty()
            if (!hasCache) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            refreshTrendingMoviesUseCase(userId)
                .onError { exception ->
                    if (!hasCache || showErrors) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }

            if (!hasCache) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun selectGenre(genre: Genre?) {
        _uiState.update { it.copy(selectedGenre = genre) }
        updateFilteredMovies()
    }

    private fun setSortOption(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        updateFilteredMovies()
    }

    private fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        updateFilteredMovies()
    }

    private fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedGenre = null,
                sortOption = SortOption.POPULARITY,
                sortOrder = SortOrder.DESCENDING
            )
        }
        updateFilteredMovies()
    }

    private fun updateFilteredMovies() {
        _uiState.update { state ->
            val filtered = applyFiltersAndSort(state.movies, state)
            state.copy(filteredMovies = filtered)
        }
    }

    private fun applyFiltersAndSort(
        movies: List<Movie>,
        state: TrendingUiState
    ): List<Movie> {
        var filteredMovies = movies

        // Apply genre filter
        state.selectedGenre?.let { genre ->
            filteredMovies = filteredMovies.filter { movie ->
                movie.genres.any { it.id == genre.id }
            }
        }

        // Apply sorting
        filteredMovies = when (state.sortOption) {
            SortOption.POPULARITY -> {
                if (state.sortOrder == SortOrder.DESCENDING) {
                    filteredMovies.sortedByDescending { it.popularity }
                } else {
                    filteredMovies.sortedBy { it.popularity }
                }
            }
            SortOption.TITLE -> {
                if (state.sortOrder == SortOrder.ASCENDING) {
                    filteredMovies.sortedBy { it.title.lowercase() }
                } else {
                    filteredMovies.sortedByDescending { it.title.lowercase() }
                }
            }
            SortOption.RELEASE_DATE -> {
                if (state.sortOrder == SortOrder.DESCENDING) {
                    filteredMovies.sortedByDescending { it.releaseDate }
                } else {
                    filteredMovies.sortedBy { it.releaseDate }
                }
            }
        }
        return filteredMovies
    }

    private fun extractGenresFromMovies(movies: List<Movie>): List<Genre> {
        return movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .sortedBy { it.name }
    }
}

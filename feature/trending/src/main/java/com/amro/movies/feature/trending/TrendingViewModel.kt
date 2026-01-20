package com.amro.movies.feature.trending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.usecase.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendingViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrendingUiState())
    val uiState: StateFlow<TrendingUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun onEvent(event: TrendingEvent) {
        when (event) {
            is TrendingEvent.LoadMovies -> loadMovies()
            is TrendingEvent.Retry -> loadMovies()
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

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getTrendingMoviesUseCase()
                .onSuccess { movies ->
                    val availableGenres = extractGenresFromMovies(movies)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = movies,
                            availableGenres = availableGenres,
                            error = null
                        )
                    }
                    applyFiltersAndSort()
                }
                .onError { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                }
        }
    }

    private fun selectGenre(genre: Genre?) {
        _uiState.update { it.copy(selectedGenre = genre) }
        applyFiltersAndSort()
    }

    private fun setSortOption(option: SortOption) {
        _uiState.update { it.copy(sortOption = option) }
        applyFiltersAndSort()
    }

    private fun setSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        applyFiltersAndSort()
    }

    private fun clearFilters() {
        _uiState.update {
            it.copy(
                selectedGenre = null,
                sortOption = SortOption.POPULARITY,
                sortOrder = SortOrder.DESCENDING
            )
        }
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        val currentState = _uiState.value
        var filteredMovies = currentState.movies

        // Apply genre filter
        currentState.selectedGenre?.let { genre ->
            filteredMovies = filteredMovies.filter { movie ->
                movie.genres.any { it.id == genre.id }
            }
        }

        // Apply sorting
        filteredMovies = when (currentState.sortOption) {
            SortOption.POPULARITY -> {
                if (currentState.sortOrder == SortOrder.DESCENDING) {
                    filteredMovies.sortedByDescending { it.popularity }
                } else {
                    filteredMovies.sortedBy { it.popularity }
                }
            }
            SortOption.TITLE -> {
                if (currentState.sortOrder == SortOrder.ASCENDING) {
                    filteredMovies.sortedBy { it.title.lowercase() }
                } else {
                    filteredMovies.sortedByDescending { it.title.lowercase() }
                }
            }
            SortOption.RELEASE_DATE -> {
                if (currentState.sortOrder == SortOrder.DESCENDING) {
                    filteredMovies.sortedByDescending { it.releaseDate }
                } else {
                    filteredMovies.sortedBy { it.releaseDate }
                }
            }
        }

        _uiState.update { it.copy(filteredMovies = filteredMovies) }
    }

    private fun extractGenresFromMovies(movies: List<Movie>): List<Genre> {
        return movies
            .flatMap { it.genres }
            .distinctBy { it.id }
            .sortedBy { it.name }
    }
}

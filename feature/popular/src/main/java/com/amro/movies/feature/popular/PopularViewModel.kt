package com.amro.movies.feature.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.usecase.GetPopularMoviesUseCase
import com.amro.movies.domain.usecase.RefreshPopularMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val refreshPopularMoviesUseCase: RefreshPopularMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularUiState())
    val uiState: StateFlow<PopularUiState> = _uiState.asStateFlow()

    private val userId = Constants.DEFAULT_USER_ID

    init {
        observeMovies()
        refreshMovies(showErrors = false)
    }

    fun onEvent(event: PopularEvent) {
        when (event) {
            PopularEvent.LoadMovies -> refreshMovies(showErrors = false)
            PopularEvent.Retry -> refreshMovies(showErrors = true)
        }
    }

    private fun observeMovies() {
        viewModelScope.launch {
            getPopularMoviesUseCase(userId).collect { movies ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = state.isLoading && movies.isEmpty(),
                        movies = movies,
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

            refreshPopularMoviesUseCase(userId)
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
}

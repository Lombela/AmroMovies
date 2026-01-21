package com.amro.movies.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.usecase.AddFavoriteMovieUseCase
import com.amro.movies.domain.usecase.GetMovieDetailsUseCase
import com.amro.movies.domain.usecase.ObserveIsFavoriteMovieUseCase
import com.amro.movies.domain.usecase.RefreshMovieDetailsUseCase
import com.amro.movies.domain.usecase.RemoveFavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val refreshMovieDetailsUseCase: RefreshMovieDetailsUseCase,
    private val observeIsFavoriteMovieUseCase: ObserveIsFavoriteMovieUseCase,
    private val addFavoriteMovieUseCase: AddFavoriteMovieUseCase,
    private val removeFavoriteMovieUseCase: RemoveFavoriteMovieUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: 0
    private val userId = Constants.DEFAULT_USER_ID

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    private var hasPerformedInitialRefresh = false

    init {
        observeMovieDetails()
        observeFavoriteStatus()
    }

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.Retry -> refreshMovieDetails(showErrors = true)
            is DetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun observeMovieDetails() {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).collect { details ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = state.isLoading && details == null,
                        movieDetails = details,
                        error = if (details != null) null else state.error
                    )
                }

                if (!hasPerformedInitialRefresh) {
                    hasPerformedInitialRefresh = true
                    refreshMovieDetails(showErrors = details == null)
                }
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            observeIsFavoriteMovieUseCase(userId, movieId).collect { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun toggleFavorite() {
        val details = _uiState.value.movieDetails ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                removeFavoriteMovieUseCase(userId, movieId)
            } else {
                addFavoriteMovieUseCase(userId, details)
            }
        }
    }

    private fun refreshMovieDetails(showErrors: Boolean) {
        viewModelScope.launch {
            val hasCache = _uiState.value.movieDetails != null
            if (!hasCache) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            }

            refreshMovieDetailsUseCase(movieId)
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

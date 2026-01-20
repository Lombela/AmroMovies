package com.amro.movies.feature.detail

import com.amro.movies.domain.model.MovieDetails

data class DetailUiState(
    val isLoading: Boolean = true,
    val movieDetails: MovieDetails? = null,
    val error: String? = null
)

sealed class DetailEvent {
    data object Retry : DetailEvent()
}

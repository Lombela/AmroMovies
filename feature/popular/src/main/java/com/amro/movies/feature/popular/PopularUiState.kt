package com.amro.movies.feature.popular

import com.amro.movies.domain.model.Movie

data class PopularUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val error: String? = null
)

sealed class PopularEvent {
    data object LoadMovies : PopularEvent()
    data object Retry : PopularEvent()
}

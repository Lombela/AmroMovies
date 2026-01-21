package com.amro.movies.feature.library

import com.amro.movies.domain.model.Movie

data class LibraryUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList()
)

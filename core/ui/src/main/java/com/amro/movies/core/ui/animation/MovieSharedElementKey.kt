package com.amro.movies.core.ui.animation

enum class MovieSharedElementType {
    Poster
}

data class MovieSharedElementKey(
    val movieId: Int,
    val type: MovieSharedElementType
)

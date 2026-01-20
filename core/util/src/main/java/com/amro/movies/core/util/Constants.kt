package com.amro.movies.core.util

object Constants {
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    object ImageSize {
        const val POSTER_W185 = "w185"
        const val POSTER_W342 = "w342"
        const val POSTER_W500 = "w500"
        const val BACKDROP_W780 = "w780"
        const val ORIGINAL = "original"
    }

    fun buildImageUrl(path: String?, size: String = ImageSize.POSTER_W342): String? {
        return path?.let { "${TMDB_IMAGE_BASE_URL}$size$it" }
    }

    fun buildPosterUrl(path: String?): String? = buildImageUrl(path, ImageSize.POSTER_W342)

    fun buildBackdropUrl(path: String?): String? = buildImageUrl(path, ImageSize.BACKDROP_W780)
}

package com.amro.movies.core.navigation

sealed class NavRoutes(val route: String) {
    data object Trending : NavRoutes("trending")
    data object Detail : NavRoutes("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}

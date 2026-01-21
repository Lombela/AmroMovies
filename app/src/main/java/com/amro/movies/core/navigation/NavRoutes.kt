package com.amro.movies.core.navigation

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Library : NavRoutes("library")
    data object Detail : NavRoutes("detail/{movieId}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}

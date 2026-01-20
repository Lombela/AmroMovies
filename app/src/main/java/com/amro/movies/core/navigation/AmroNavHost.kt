package com.amro.movies.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amro.movies.feature.detail.DetailScreen
import com.amro.movies.feature.trending.TrendingScreen

@Composable
fun AmroNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Trending.route
    ) {
        composable(route = NavRoutes.Trending.route) {
            TrendingScreen(
                onMovieClick = { movieId ->
                    navController.navigate(NavRoutes.Detail.createRoute(movieId))
                }
            )
        }

        composable(
            route = NavRoutes.Detail.route,
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                }
            )
        ) {
            DetailScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

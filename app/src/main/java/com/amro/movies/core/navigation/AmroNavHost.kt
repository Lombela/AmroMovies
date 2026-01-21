package com.amro.movies.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.amro.movies.feature.detail.DetailScreen
import com.amro.movies.feature.library.LibraryScreen
import com.amro.movies.home.HomeScreen
import com.amro.movies.R

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun AmroNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val density = LocalDensity.current
    val bottomBarHeight = remember { mutableStateOf(0.dp) }

    val bottomNavItems = listOf(
        BottomNavItem(
            route = NavRoutes.Home.route,
            labelRes = R.string.nav_home,
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            route = NavRoutes.Library.route,
            labelRes = R.string.nav_library,
            icon = Icons.Default.VideoLibrary
        )
    )

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    SharedTransitionLayout {
        val sharedTransitionScope = this

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        modifier = Modifier.onSizeChanged { size ->
                            bottomBarHeight.value = with(density) { size.height.toDp() }
                        }
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = stringResource(item.labelRes)
                                    )
                                },
                                label = { Text(text = stringResource(item.labelRes)) }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Home.route
            ) {
                composable(route = NavRoutes.Home.route) {
                    HomeScreen(
                        onMovieClick = { movieId ->
                            navController.navigate(NavRoutes.Detail.createRoute(movieId))
                        },
                        bottomBarHeight = bottomBarHeight.value,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this
                    )
                }

                composable(route = NavRoutes.Library.route) {
                    LibraryScreen(
                        onMovieClick = { movieId ->
                            navController.navigate(NavRoutes.Detail.createRoute(movieId))
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this,
                        bottomBarHeight = bottomBarHeight.value
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
                        },
                        sharedTransitionScope = sharedTransitionScope,
                        animatedVisibilityScope = this,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

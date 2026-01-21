package com.amro.movies.feature.trending

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amro.movies.core.ui.components.EmptyView
import com.amro.movies.core.ui.components.ErrorView
import com.amro.movies.core.ui.components.LoadingIndicator
import com.amro.movies.feature.trending.components.FilterBottomSheet
import com.amro.movies.feature.trending.components.MovieCard
import com.amro.movies.feature.trending.components.SortBottomSheet

@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun TrendingScreen(
    onMovieClick: (Int) -> Unit,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    modifier: Modifier = Modifier,
    viewModel: TrendingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator()
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: stringResource(R.string.error_loading_movies),
                        onRetry = { viewModel.onEvent(TrendingEvent.Retry) }
                    )
                }
                uiState.filteredMovies.isEmpty() && uiState.selectedGenre != null -> {
                    EmptyView(
                        message = stringResource(R.string.try_adjusting_filters),
                        onClearFilters = { viewModel.onEvent(TrendingEvent.ClearFilters) }
                    )
                }
                uiState.filteredMovies.isEmpty() -> {
                    EmptyView(
                        message = stringResource(R.string.no_movies_found)
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.filteredMovies,
                            key = { it.id }
                        ) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = { onMovieClick(movie.id) },
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }
                    }
                }
            }
        }

        // Filter Bottom Sheet
        if (uiState.isFilterSheetVisible) {
            FilterBottomSheet(
                genres = uiState.availableGenres,
                selectedGenre = uiState.selectedGenre,
                onGenreSelected = { genre ->
                    viewModel.onEvent(TrendingEvent.SelectGenre(genre))
                },
                onDismiss = { viewModel.onEvent(TrendingEvent.HideFilterSheet) }
            )
        }

        // Sort Bottom Sheet
        if (uiState.isSortSheetVisible) {
            SortBottomSheet(
                currentSortOption = uiState.sortOption,
                currentSortOrder = uiState.sortOrder,
                onSortOptionSelected = { option ->
                    viewModel.onEvent(TrendingEvent.SetSortOption(option))
                },
                onSortOrderSelected = { order ->
                    viewModel.onEvent(TrendingEvent.SetSortOrder(order))
                },
                onDismiss = { viewModel.onEvent(TrendingEvent.HideSortSheet) }
            )
        }
    }
}

package com.amro.movies.feature.popular

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amro.movies.core.ui.components.EmptyView
import com.amro.movies.core.ui.components.ErrorView
import com.amro.movies.core.ui.components.LoadingIndicator
import com.amro.movies.feature.popular.components.PopularMoviePage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PopularScreen(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PopularViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                LoadingIndicator()
            }
            uiState.error != null -> {
                ErrorView(
                    message = uiState.error ?: stringResource(R.string.error_loading_popular_movies),
                    onRetry = { viewModel.onEvent(PopularEvent.Retry) }
                )
            }
            uiState.movies.isEmpty() -> {
                EmptyView(
                    message = stringResource(R.string.no_popular_movies)
                )
            }
            else -> {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { uiState.movies.size }
                )

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    key = { page -> uiState.movies[page].id }
                ) { page ->
                    val movie = uiState.movies[page]
                    PopularMoviePage(
                        movie = movie,
                        onWatchClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}

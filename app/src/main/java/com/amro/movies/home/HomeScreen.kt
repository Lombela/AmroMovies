package com.amro.movies.home

import android.app.Activity
import android.graphics.Color as AndroidColor
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.amro.movies.R
import com.amro.movies.feature.popular.PopularScreen
import com.amro.movies.feature.trending.TrendingEvent
import com.amro.movies.feature.trending.TrendingScreen
import com.amro.movies.feature.trending.TrendingViewModel
import com.amro.movies.feature.trending.R as TrendingR

private enum class HomeTab(@StringRes val titleRes: Int) {
    Trending(R.string.tab_trending),
    Popular(R.string.tab_popular)
}

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    bottomBarHeight: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val trendingViewModel: TrendingViewModel = hiltViewModel()
    val view = LocalView.current
    val darkTheme = isSystemInDarkTheme()
    var selectedTab by rememberSaveable { mutableStateOf(HomeTab.Trending) }
    val tabs = HomeTab.entries
    val tabRowHeight = 52.dp
    val isPopular = selectedTab == HomeTab.Popular
    val isTrending = selectedTab == HomeTab.Trending
    val selectedColor = if (isPopular) Color.White else MaterialTheme.colorScheme.primary
    val unselectedColor = if (isPopular) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
    val containerColor = if (isPopular) Color.Transparent else MaterialTheme.colorScheme.surface

    if (!view.isInEditMode) {
        DisposableEffect(view, darkTheme) {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            val previousStatusBarColor = window.statusBarColor
            val previousLightStatus = insetsController.isAppearanceLightStatusBars

            window.statusBarColor = AndroidColor.TRANSPARENT
            insetsController.isAppearanceLightStatusBars = !darkTheme

            onDispose {
                window.statusBarColor = previousStatusBarColor
                insetsController.isAppearanceLightStatusBars = previousLightStatus
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (selectedTab) {
            HomeTab.Trending -> {
                TrendingScreen(
                    onMovieClick = onMovieClick,
                    viewModel = trendingViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = tabRowHeight)
                        .statusBarsPadding()
                )
            }
            HomeTab.Popular -> {
                PopularScreen(
                    onMovieClick = onMovieClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomBarHeight)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tabRowHeight)
                    .background(containerColor)
            ) {
                if (isTrending) {
                    IconButton(
                        onClick = { trendingViewModel.onEvent(TrendingEvent.ShowFilterSheet) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(TrendingR.string.filter)
                        )
                    }

                    IconButton(
                        onClick = { trendingViewModel.onEvent(TrendingEvent.ShowSortSheet) },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = stringResource(TrendingR.string.sort)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEach { tab ->
                        val isSelected = tab == selectedTab
                        Column(
                            modifier = Modifier
                                .clickable { selectedTab = tab }
                                .padding(horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(tab.titleRes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) selectedColor else unselectedColor
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(
                                        color = if (isSelected) selectedColor else Color.Transparent,
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.amro.movies.feature.trending

import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie

data class TrendingUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val filteredMovies: List<Movie> = emptyList(),
    val availableGenres: List<Genre> = emptyList(),
    val selectedGenre: Genre? = null,
    val sortOption: SortOption = SortOption.POPULARITY,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val error: String? = null,
    val isFilterSheetVisible: Boolean = false,
    val isSortSheetVisible: Boolean = false
)

enum class SortOption {
    POPULARITY,
    TITLE,
    RELEASE_DATE
}

enum class SortOrder {
    ASCENDING,
    DESCENDING
}

sealed class TrendingEvent {
    data object LoadMovies : TrendingEvent()
    data object Retry : TrendingEvent()
    data class SelectGenre(val genre: Genre?) : TrendingEvent()
    data class SetSortOption(val option: SortOption) : TrendingEvent()
    data class SetSortOrder(val order: SortOrder) : TrendingEvent()
    data object ShowFilterSheet : TrendingEvent()
    data object HideFilterSheet : TrendingEvent()
    data object ShowSortSheet : TrendingEvent()
    data object HideSortSheet : TrendingEvent()
    data object ClearFilters : TrendingEvent()
}

package com.amro.movies.feature.trending

import app.cash.turbine.test
import com.amro.movies.TestData
import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.usecase.GetTrendingMoviesUseCase
import com.amro.movies.domain.usecase.RefreshTrendingMoviesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TrendingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getTrendingMoviesUseCase: GetTrendingMoviesUseCase
    private lateinit var refreshTrendingMoviesUseCase: RefreshTrendingMoviesUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTrendingMoviesUseCase = mockk()
        refreshTrendingMoviesUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        every { getTrendingMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        // When
        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads movies on initialization`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        // When
        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.movies.size)
            assertEquals(2, state.filteredMovies.size)
            assertNull(state.error)
        }
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        every { getTrendingMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Error(exception)

        // When
        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.error)
        }
    }

    @Test
    fun `filters movies by genre`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(TrendingEvent.SelectGenre(TestData.genre3)) // Drama
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.filteredMovies.size)
            assertEquals(TestData.movie2.id, state.filteredMovies[0].id)
        }
    }

    @Test
    fun `clears filter shows all movies`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(TrendingEvent.SelectGenre(TestData.genre3))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(TrendingEvent.ClearFilters)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(2, state.filteredMovies.size)
            assertNull(state.selectedGenre)
        }
    }

    @Test
    fun `sorts movies by title ascending`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(TrendingEvent.SetSortOption(SortOption.TITLE))
        viewModel.onEvent(TrendingEvent.SetSortOrder(SortOrder.ASCENDING))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Another Movie", state.filteredMovies[0].title)
            assertEquals("Test Movie 1", state.filteredMovies[1].title)
        }
    }

    @Test
    fun `sorts movies by popularity descending by default`() = runTest {
        // Given
        val movies = listOf(TestData.movie2, TestData.movie1) // movie2 has lower popularity
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(SortOption.POPULARITY, state.sortOption)
            assertEquals(SortOrder.DESCENDING, state.sortOrder)
            assertEquals(TestData.movie1.id, state.filteredMovies[0].id) // Higher popularity first
        }
    }

    @Test
    fun `extracts available genres from movies`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getTrendingMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(3, state.availableGenres.size)
        }
    }

    @Test
    fun `shows filter sheet when requested`() = runTest {
        // Given
        every { getTrendingMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.onEvent(TrendingEvent.ShowFilterSheet)

        // Then
        assertTrue(viewModel.uiState.value.isFilterSheetVisible)
    }

    @Test
    fun `hides filter sheet when requested`() = runTest {
        // Given
        every { getTrendingMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onEvent(TrendingEvent.ShowFilterSheet)

        // When
        viewModel.onEvent(TrendingEvent.HideFilterSheet)

        // Then
        assertFalse(viewModel.uiState.value.isFilterSheetVisible)
    }

    @Test
    fun `retry reloads movies`() = runTest {
        // Given
        val moviesFlow = MutableStateFlow<List<Movie>>(emptyList())
        every { getTrendingMoviesUseCase(any()) } returns moviesFlow
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Error(RuntimeException("Error"))

        val viewModel = TrendingViewModel(getTrendingMoviesUseCase, refreshTrendingMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val movies = listOf(TestData.movie1)
        coEvery { refreshTrendingMoviesUseCase(any()) } returns Result.Success(Unit)

        // When
        viewModel.onEvent(TrendingEvent.Retry)
        moviesFlow.value = movies
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.movies.size)
            assertNull(state.error)
        }
    }
}

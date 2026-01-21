package com.amro.movies.feature.popular

import app.cash.turbine.test
import com.amro.movies.TestData
import com.amro.movies.core.util.Constants
import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.usecase.GetPopularMoviesUseCase
import com.amro.movies.domain.usecase.RefreshPopularMoviesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class PopularViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var refreshPopularMoviesUseCase: RefreshPopularMoviesUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getPopularMoviesUseCase = mockk()
        refreshPopularMoviesUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        every { getPopularMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = PopularViewModel(getPopularMoviesUseCase, refreshPopularMoviesUseCase)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads movies on initialization`() = runTest {
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getPopularMoviesUseCase(any()) } returns flowOf(movies)
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Success(Unit)

        val viewModel = PopularViewModel(getPopularMoviesUseCase, refreshPopularMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.movies.size)
            assertNull(state.error)
        }
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        val exception = RuntimeException("Network error")
        every { getPopularMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Error(exception)

        val viewModel = PopularViewModel(getPopularMoviesUseCase, refreshPopularMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Network error", state.error)
        }
    }

    @Test
    fun `retry reloads movies`() = runTest {
        val moviesFlow = MutableStateFlow<List<Movie>>(emptyList())
        every { getPopularMoviesUseCase(any()) } returns moviesFlow
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Error(RuntimeException("Error"))

        val viewModel = PopularViewModel(getPopularMoviesUseCase, refreshPopularMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        val movies = listOf(TestData.movie1)
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Success(Unit)

        viewModel.onEvent(PopularEvent.Retry)
        moviesFlow.value = movies
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(1, state.movies.size)
            assertNull(state.error)
        }
    }

    @Test
    fun `requests movies with default user id`() = runTest {
        every { getPopularMoviesUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshPopularMoviesUseCase(any()) } returns Result.Success(Unit)

        PopularViewModel(getPopularMoviesUseCase, refreshPopularMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 1) {
            getPopularMoviesUseCase(Constants.DEFAULT_USER_ID)
        }
        coVerify(exactly = 1) {
            refreshPopularMoviesUseCase(Constants.DEFAULT_USER_ID)
        }
    }
}

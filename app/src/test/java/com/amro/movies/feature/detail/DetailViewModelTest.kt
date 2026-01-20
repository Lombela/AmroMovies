package com.amro.movies.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.amro.movies.TestData
import com.amro.movies.core.util.Result
import com.amro.movies.domain.usecase.GetMovieDetailsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getMovieDetailsUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(movieId: Int = 1): DetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to movieId))
        return DetailViewModel(getMovieDetailsUseCase, savedStateHandle)
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        coEvery { getMovieDetailsUseCase(any()) } returns Result.Success(TestData.movieDetails)

        // When
        val viewModel = createViewModel()

        // Then
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads movie details on initialization`() = runTest {
        // Given
        val movieDetails = TestData.movieDetails
        coEvery { getMovieDetailsUseCase(1) } returns Result.Success(movieDetails)

        // When
        val viewModel = createViewModel(movieId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.movieDetails)
            assertEquals(movieDetails.id, state.movieDetails?.id)
            assertEquals(movieDetails.title, state.movieDetails?.title)
            assertNull(state.error)
        }
    }

    @Test
    fun `shows error when loading fails`() = runTest {
        // Given
        val exception = RuntimeException("Movie not found")
        coEvery { getMovieDetailsUseCase(1) } returns Result.Error(exception)

        // When
        val viewModel = createViewModel(movieId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNull(state.movieDetails)
            assertEquals("Movie not found", state.error)
        }
    }

    @Test
    fun `calls use case with correct movie id`() = runTest {
        // Given
        coEvery { getMovieDetailsUseCase(42) } returns Result.Success(TestData.movieDetails)

        // When
        val viewModel = createViewModel(movieId = 42)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { getMovieDetailsUseCase(42) }
    }

    @Test
    fun `retry reloads movie details`() = runTest {
        // Given
        coEvery { getMovieDetailsUseCase(1) } returns Result.Error(RuntimeException("Error"))

        val viewModel = createViewModel(movieId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { getMovieDetailsUseCase(1) } returns Result.Success(TestData.movieDetails)

        // When
        viewModel.onEvent(DetailEvent.Retry)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertNotNull(state.movieDetails)
            assertNull(state.error)
        }
    }

    @Test
    fun `handles movie id of 0 when not provided`() = runTest {
        // Given
        val savedStateHandle = SavedStateHandle() // No movieId
        coEvery { getMovieDetailsUseCase(0) } returns Result.Error(RuntimeException("Invalid movie ID"))

        // When
        val viewModel = DetailViewModel(getMovieDetailsUseCase, savedStateHandle)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { getMovieDetailsUseCase(0) }
    }

    @Test
    fun `movie details contains all expected fields`() = runTest {
        // Given
        val movieDetails = TestData.movieDetails
        coEvery { getMovieDetailsUseCase(1) } returns Result.Success(movieDetails)

        // When
        val viewModel = createViewModel(movieId = 1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            val details = state.movieDetails!!
            assertEquals(movieDetails.tagline, details.tagline)
            assertEquals(movieDetails.overview, details.overview)
            assertEquals(movieDetails.budget, details.budget)
            assertEquals(movieDetails.revenue, details.revenue)
            assertEquals(movieDetails.runtime, details.runtime)
            assertEquals(movieDetails.imdbId, details.imdbId)
        }
    }
}

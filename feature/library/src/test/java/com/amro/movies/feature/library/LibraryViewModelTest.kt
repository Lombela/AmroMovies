package com.amro.movies.feature.library

import app.cash.turbine.test
import com.amro.movies.TestData
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.usecase.GetFavoriteMoviesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFavoriteMoviesUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        every { getFavoriteMoviesUseCase(any(), any()) } returns flowOf(emptyList())

        val viewModel = LibraryViewModel(getFavoriteMoviesUseCase)

        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loads favorite movies on initialization`() = runTest {
        val movies = listOf(TestData.movie1, TestData.movie2)
        every { getFavoriteMoviesUseCase(any(), any()) } returns flowOf(movies)

        val viewModel = LibraryViewModel(getFavoriteMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.movies.size)
        }
    }

    @Test
    fun `returns empty state when no favorites exist`() = runTest {
        every { getFavoriteMoviesUseCase(any(), any()) } returns flowOf(emptyList())

        val viewModel = LibraryViewModel(getFavoriteMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.movies.isEmpty())
        }
    }

    @Test
    fun `requests favorites with default user id`() = runTest {
        every { getFavoriteMoviesUseCase(any(), any()) } returns flowOf(emptyList())

        LibraryViewModel(getFavoriteMoviesUseCase)
        testDispatcher.scheduler.advanceUntilIdle()

        verify(exactly = 1) {
            getFavoriteMoviesUseCase(Constants.DEFAULT_USER_ID, Int.MAX_VALUE)
        }
    }
}

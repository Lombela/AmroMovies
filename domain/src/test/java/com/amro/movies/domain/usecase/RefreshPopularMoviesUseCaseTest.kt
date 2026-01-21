package com.amro.movies.domain.usecase

import com.amro.movies.core.util.Result
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RefreshPopularMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var useCase: RefreshPopularMoviesUseCase
    private val userId = "user-1"

    @BeforeEach
    fun setup() {
        movieRepository = mockk()
        useCase = RefreshPopularMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke returns success when repository refresh succeeds`() = runTest {
        // Given
        coEvery {
            movieRepository.refreshMovieList(userId, MovieListType.POPULAR)
        } returns Result.Success(Unit)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Success)
        coVerify(exactly = 1) {
            movieRepository.refreshMovieList(userId, MovieListType.POPULAR)
        }
    }

    @Test
    fun `invoke returns error when repository refresh fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery {
            movieRepository.refreshMovieList(userId, MovieListType.POPULAR)
        } returns Result.Error(exception)

        // When
        val result = useCase(userId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify(exactly = 1) {
            movieRepository.refreshMovieList(userId, MovieListType.POPULAR)
        }
    }
}

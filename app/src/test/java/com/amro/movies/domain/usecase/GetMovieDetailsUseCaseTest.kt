package com.amro.movies.domain.usecase

import com.amro.movies.TestData
import com.amro.movies.core.util.Result
import com.amro.movies.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetMovieDetailsUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var useCase: GetMovieDetailsUseCase

    @BeforeEach
    fun setup() {
        movieRepository = mockk()
        useCase = GetMovieDetailsUseCase(movieRepository)
    }

    @Test
    fun `invoke returns success with movie details from repository`() = runTest {
        // Given
        val movieId = 1
        val movieDetails = TestData.movieDetails
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Success(movieDetails)

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(movieDetails, (result as Result.Success).data)
        coVerify(exactly = 1) { movieRepository.getMovieDetails(movieId) }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val movieId = 1
        val exception = RuntimeException("Movie not found")
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Error(exception)

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result.isError)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `invoke calls repository with correct movie id`() = runTest {
        // Given
        val movieId = 42
        coEvery { movieRepository.getMovieDetails(movieId) } returns Result.Success(TestData.movieDetails)

        // When
        useCase(movieId)

        // Then
        coVerify(exactly = 1) { movieRepository.getMovieDetails(42) }
    }
}

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

class GetTrendingMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var useCase: GetTrendingMoviesUseCase

    @BeforeEach
    fun setup() {
        movieRepository = mockk()
        useCase = GetTrendingMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke returns success with movies from repository`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        coEvery { movieRepository.getTrendingMovies() } returns Result.Success(movies)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(movies, (result as Result.Success).data)
        coVerify(exactly = 1) { movieRepository.getTrendingMovies() }
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        coEvery { movieRepository.getTrendingMovies() } returns Result.Error(exception)

        // When
        val result = useCase()

        // Then
        assertTrue(result.isError)
        assertEquals(exception, (result as Result.Error).exception)
    }

    @Test
    fun `invoke returns empty list when no movies available`() = runTest {
        // Given
        coEvery { movieRepository.getTrendingMovies() } returns Result.Success(emptyList())

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertTrue((result as Result.Success).data.isEmpty())
    }
}

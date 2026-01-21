package com.amro.movies.domain.usecase

import com.amro.movies.TestData
import com.amro.movies.domain.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
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
        every { movieRepository.observeMovieDetails(movieId) } returns flowOf(movieDetails)

        // When
        val result = useCase(movieId).first()

        // Then
        assertEquals(movieDetails, result)
        verify(exactly = 1) { movieRepository.observeMovieDetails(movieId) }
    }

    @Test
    fun `invoke returns null when repository has no cached details`() = runTest {
        // Given
        val movieId = 1
        every { movieRepository.observeMovieDetails(movieId) } returns flowOf(null)

        // When
        val result = useCase(movieId).first()

        // Then
        assertNull(result)
    }

    @Test
    fun `invoke calls repository with correct movie id`() = runTest {
        // Given
        val movieId = 42
        every { movieRepository.observeMovieDetails(movieId) } returns flowOf(TestData.movieDetails)

        // When
        useCase(movieId).first()

        // Then
        verify(exactly = 1) { movieRepository.observeMovieDetails(42) }
    }
}

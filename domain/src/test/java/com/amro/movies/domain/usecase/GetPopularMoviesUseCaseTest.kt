package com.amro.movies.domain.usecase

import com.amro.movies.TestData
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetPopularMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var useCase: GetPopularMoviesUseCase
    private val userId = "user-1"

    @BeforeEach
    fun setup() {
        movieRepository = mockk()
        useCase = GetPopularMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke returns movies from repository`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every {
            movieRepository.observeMovieList(userId, MovieListType.POPULAR, 100)
        } returns flowOf(movies)

        // When
        val result = useCase(userId).first()

        // Then
        assertEquals(movies, result)
        verify(exactly = 1) {
            movieRepository.observeMovieList(userId, MovieListType.POPULAR, 100)
        }
    }

    @Test
    fun `invoke returns empty list when repository has no data`() = runTest {
        // Given
        every {
            movieRepository.observeMovieList(userId, MovieListType.POPULAR, 100)
        } returns flowOf(emptyList())

        // When
        val result = useCase(userId).first()

        // Then
        assertEquals(0, result.size)
    }
}

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

class GetFavoriteMoviesUseCaseTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var useCase: GetFavoriteMoviesUseCase
    private val userId = "user-1"

    @BeforeEach
    fun setup() {
        movieRepository = mockk()
        useCase = GetFavoriteMoviesUseCase(movieRepository)
    }

    @Test
    fun `invoke returns movies from repository`() = runTest {
        // Given
        val movies = listOf(TestData.movie1, TestData.movie2)
        every {
            movieRepository.observeMovieList(userId, MovieListType.FAVORITES, Int.MAX_VALUE)
        } returns flowOf(movies)

        // When
        val result = useCase(userId).first()

        // Then
        assertEquals(movies, result)
        verify(exactly = 1) {
            movieRepository.observeMovieList(userId, MovieListType.FAVORITES, Int.MAX_VALUE)
        }
    }

    @Test
    fun `invoke forwards custom limit`() = runTest {
        // Given
        val limit = 25
        every {
            movieRepository.observeMovieList(userId, MovieListType.FAVORITES, limit)
        } returns flowOf(emptyList())

        // When
        useCase(userId, limit).first()

        // Then
        verify(exactly = 1) {
            movieRepository.observeMovieList(userId, MovieListType.FAVORITES, limit)
        }
    }
}

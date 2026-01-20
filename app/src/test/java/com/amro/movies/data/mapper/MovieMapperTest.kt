package com.amro.movies.data.mapper

import com.amro.movies.TestData
import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.domain.model.Genre
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MovieMapperTest {

    private val genreMap = mapOf(
        28 to Genre(28, "Action"),
        35 to Genre(35, "Comedy"),
        18 to Genre(18, "Drama")
    )

    @Test
    fun `toDomain maps MovieResource to Movie correctly`() {
        // Given
        val movieResource = TestData.movieResource1

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertEquals(movieResource.id, movie.id)
        assertEquals(movieResource.title, movie.title)
        assertEquals(movieResource.posterPath, movie.posterPath)
        assertEquals(movieResource.popularity, movie.popularity)
        assertEquals(movieResource.voteAverage, movie.voteAverage)
        assertEquals(LocalDate.of(2024, 1, 15), movie.releaseDate)
        assertEquals(2, movie.genres.size)
    }

    @Test
    fun `toDomain maps genres correctly using genre map`() {
        // Given
        val movieResource = TestData.movieResource1

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertEquals(2, movie.genres.size)
        assertEquals("Action", movie.genres[0].name)
        assertEquals("Comedy", movie.genres[1].name)
    }

    @Test
    fun `toDomain returns empty genres when genre ids not in map`() {
        // Given
        val movieResource = MovieResource(
            id = 1,
            title = "Test",
            genreIds = listOf(999, 888)
        )

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertEquals(0, movie.genres.size)
    }

    @Test
    fun `toDomain handles null release date`() {
        // Given
        val movieResource = MovieResource(
            id = 1,
            title = "Test",
            releaseDate = null
        )

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertNull(movie.releaseDate)
    }

    @Test
    fun `toDomain handles empty release date`() {
        // Given
        val movieResource = MovieResource(
            id = 1,
            title = "Test",
            releaseDate = ""
        )

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertNull(movie.releaseDate)
    }

    @Test
    fun `toDomain handles invalid release date format`() {
        // Given
        val movieResource = MovieResource(
            id = 1,
            title = "Test",
            releaseDate = "invalid-date"
        )

        // When
        val movie = movieResource.toDomain(genreMap)

        // Then
        assertNull(movie.releaseDate)
    }

    @Test
    fun `toDomainList maps list of MovieResource correctly`() {
        // Given
        val movieResources = listOf(TestData.movieResource1, TestData.movieResource2)

        // When
        val movies = movieResources.toDomainList(genreMap)

        // Then
        assertEquals(2, movies.size)
        assertEquals(TestData.movieResource1.id, movies[0].id)
        assertEquals(TestData.movieResource2.id, movies[1].id)
    }
}

package com.amro.movies.data.mapper

import com.amro.movies.TestData
import com.amro.movies.data.remote.resource.MovieDetailsResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MovieDetailsMapperTest {

    @Test
    fun `toDomain maps MovieDetailsResource correctly`() {
        // Given
        val resource = TestData.movieDetailsResource

        // When
        val details = resource.toDomain()

        // Then
        assertEquals(resource.id, details.id)
        assertEquals(resource.title, details.title)
        assertEquals(resource.tagline, details.tagline)
        assertEquals(resource.posterPath, details.posterPath)
        assertEquals(resource.backdropPath, details.backdropPath)
        assertEquals(resource.overview, details.overview)
        assertEquals(resource.voteAverage, details.voteAverage)
        assertEquals(resource.voteCount, details.voteCount)
        assertEquals(resource.budget, details.budget)
        assertEquals(resource.revenue, details.revenue)
        assertEquals(resource.status, details.status)
        assertEquals(resource.imdbId, details.imdbId)
        assertEquals(resource.runtime, details.runtime)
        assertEquals(LocalDate.of(2024, 1, 15), details.releaseDate)
    }

    @Test
    fun `toDomain maps genres correctly`() {
        // Given
        val resource = TestData.movieDetailsResource

        // When
        val details = resource.toDomain()

        // Then
        assertEquals(2, details.genres.size)
        assertEquals("Action", details.genres[0].name)
        assertEquals("Comedy", details.genres[1].name)
    }

    @Test
    fun `toDomain handles null tagline`() {
        // Given
        val resource = MovieDetailsResource(
            id = 1,
            title = "Test",
            tagline = null
        )

        // When
        val details = resource.toDomain()

        // Then
        assertNull(details.tagline)
    }

    @Test
    fun `toDomain handles blank tagline`() {
        // Given
        val resource = MovieDetailsResource(
            id = 1,
            title = "Test",
            tagline = "   "
        )

        // When
        val details = resource.toDomain()

        // Then
        assertNull(details.tagline)
    }

    @Test
    fun `toDomain handles null release date`() {
        // Given
        val resource = MovieDetailsResource(
            id = 1,
            title = "Test",
            releaseDate = null
        )

        // When
        val details = resource.toDomain()

        // Then
        assertNull(details.releaseDate)
    }

    @Test
    fun `toDomain handles invalid release date`() {
        // Given
        val resource = MovieDetailsResource(
            id = 1,
            title = "Test",
            releaseDate = "not-a-date"
        )

        // When
        val details = resource.toDomain()

        // Then
        assertNull(details.releaseDate)
    }

    @Test
    fun `toDomain handles empty genres list`() {
        // Given
        val resource = MovieDetailsResource(
            id = 1,
            title = "Test",
            genres = emptyList()
        )

        // When
        val details = resource.toDomain()

        // Then
        assertEquals(0, details.genres.size)
    }
}

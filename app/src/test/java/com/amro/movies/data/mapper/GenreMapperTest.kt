package com.amro.movies.data.mapper

import com.amro.movies.TestData
import com.amro.movies.data.remote.resource.GenreResource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GenreMapperTest {

    @Test
    fun `toDomain maps GenreResource to Genre correctly`() {
        // Given
        val genreResource = TestData.genreResource1

        // When
        val genre = genreResource.toDomain()

        // Then
        assertEquals(genreResource.id, genre.id)
        assertEquals(genreResource.name, genre.name)
    }

    @Test
    fun `toDomainList maps list of GenreResource to list of Genre`() {
        // Given
        val genreResources = listOf(TestData.genreResource1, TestData.genreResource2, TestData.genreResource3)

        // When
        val genres = genreResources.toDomainList()

        // Then
        assertEquals(3, genres.size)
        assertEquals(TestData.genreResource1.id, genres[0].id)
        assertEquals(TestData.genreResource2.id, genres[1].id)
        assertEquals(TestData.genreResource3.id, genres[2].id)
    }

    @Test
    fun `toDomainList returns empty list for empty input`() {
        // Given
        val genreResources = emptyList<GenreResource>()

        // When
        val genres = genreResources.toDomainList()

        // Then
        assertEquals(0, genres.size)
    }
}

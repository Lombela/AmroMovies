package com.amro.movies.data.mapper

import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun MovieResource.toDomain(genreMap: Map<Int, Genre>): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    genres = genreIds.mapNotNull { genreMap[it] },
    popularity = popularity,
    releaseDate = releaseDate?.parseToLocalDate(),
    voteAverage = voteAverage
)

fun List<MovieResource>.toDomainList(genreMap: Map<Int, Genre>): List<Movie> =
    map { it.toDomain(genreMap) }

private fun String.parseToLocalDate(): LocalDate? {
    return try {
        if (this.isBlank()) null
        else LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: DateTimeParseException) {
        null
    }
}

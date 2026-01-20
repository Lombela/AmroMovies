package com.amro.movies.data.mapper

import com.amro.movies.data.remote.resource.MovieDetailsResource
import com.amro.movies.domain.model.MovieDetails
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun MovieDetailsResource.toDomain(): MovieDetails = MovieDetails(
    id = id,
    title = title,
    tagline = tagline?.takeIf { it.isNotBlank() },
    posterPath = posterPath,
    backdropPath = backdropPath,
    genres = genres.toDomainList(),
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    budget = budget,
    revenue = revenue,
    status = status,
    imdbId = imdbId,
    runtime = runtime,
    releaseDate = releaseDate?.parseToLocalDate()
)

private fun String.parseToLocalDate(): LocalDate? {
    return try {
        if (this.isBlank()) null
        else LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (e: DateTimeParseException) {
        null
    }
}

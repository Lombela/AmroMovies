package com.amro.movies.data.local.mapper

import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef
import com.amro.movies.data.local.model.MovieWithGenres
import com.amro.movies.data.remote.resource.GenreResource
import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun GenreResource.toEntity(): GenreEntity = GenreEntity(
    genreId = id,
    name = name
)

fun MovieResource.toEntity(
    nowEpochMs: Long,
    cacheSizeBytes: Long = estimateCacheSizeBytes()
): MovieEntity = MovieEntity(
    movieId = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    popularity = popularity,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    lastUpdatedEpochMs = nowEpochMs,
    lastAccessEpochMs = nowEpochMs,
    cacheSizeBytes = cacheSizeBytes
)

fun MovieResource.toGenreCrossRefs(): List<MovieGenreCrossRef> {
    return genreIds.map { genreId ->
        MovieGenreCrossRef(movieId = id, genreId = genreId)
    }
}

fun MovieWithGenres.toDomain(): Movie = Movie(
    id = movie.movieId,
    title = movie.title,
    posterPath = movie.posterPath,
    backdropPath = movie.backdropPath,
    overview = movie.overview,
    genres = genres.map { it.toDomain() },
    popularity = movie.popularity,
    releaseDate = movie.releaseDate?.parseToLocalDate(),
    voteAverage = movie.voteAverage
)

fun GenreEntity.toDomain(): Genre = Genre(
    id = genreId,
    name = name
)

private fun String.parseToLocalDate(): LocalDate? {
    return try {
        if (isBlank()) null else LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun MovieResource.estimateCacheSizeBytes(): Long {
    val sizeChars = title.length +
        (overview?.length ?: 0) +
        (posterPath?.length ?: 0) +
        (backdropPath?.length ?: 0) +
        genreIds.size * 4
    return (sizeChars + 64L) * 2L
}

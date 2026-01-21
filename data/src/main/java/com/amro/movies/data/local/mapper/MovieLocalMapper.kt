package com.amro.movies.data.local.mapper

import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieDetailsEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef
import com.amro.movies.data.local.model.MovieDetailsWithGenres
import com.amro.movies.data.local.model.MovieWithGenres
import com.amro.movies.data.remote.resource.GenreResource
import com.amro.movies.data.remote.resource.MovieDetailsResource
import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails
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

fun MovieDetailsResource.toEntity(nowEpochMs: Long): MovieDetailsEntity = MovieDetailsEntity(
    movieId = id,
    title = title,
    tagline = tagline,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    budget = budget,
    revenue = revenue,
    status = status,
    imdbId = imdbId,
    runtime = runtime,
    releaseDate = releaseDate,
    lastUpdatedEpochMs = nowEpochMs
)

fun MovieDetailsResource.toGenreCrossRefs(): List<MovieGenreCrossRef> {
    return genres.map { genre ->
        MovieGenreCrossRef(movieId = id, genreId = genre.id)
    }
}

fun Genre.toEntity(): GenreEntity = GenreEntity(
    genreId = id,
    name = name
)

fun MovieDetails.toEntity(
    nowEpochMs: Long,
    cacheSizeBytes: Long = estimateCacheSizeBytes()
): MovieEntity = MovieEntity(
    movieId = id,
    title = title,
    posterPath = posterPath,
    backdropPath = backdropPath,
    overview = overview,
    popularity = 0.0,
    releaseDate = releaseDate?.format(DateTimeFormatter.ISO_LOCAL_DATE),
    voteAverage = voteAverage,
    voteCount = voteCount,
    lastUpdatedEpochMs = nowEpochMs,
    lastAccessEpochMs = nowEpochMs,
    cacheSizeBytes = cacheSizeBytes
)

fun MovieDetails.toGenreCrossRefs(): List<MovieGenreCrossRef> {
    return genres.map { genre ->
        MovieGenreCrossRef(movieId = id, genreId = genre.id)
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

fun MovieDetailsWithGenres.toDomain(): MovieDetails = MovieDetails(
    id = details.movieId,
    title = details.title,
    tagline = details.tagline?.takeIf { it.isNotBlank() },
    posterPath = details.posterPath,
    backdropPath = details.backdropPath,
    genres = genres.map { it.toDomain() },
    overview = details.overview,
    voteAverage = details.voteAverage,
    voteCount = details.voteCount,
    budget = details.budget,
    revenue = details.revenue,
    status = details.status,
    imdbId = details.imdbId,
    runtime = details.runtime,
    releaseDate = details.releaseDate?.parseToLocalDate()
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

private fun MovieDetails.estimateCacheSizeBytes(): Long {
    val sizeChars = title.length +
        (overview?.length ?: 0) +
        (posterPath?.length ?: 0) +
        (backdropPath?.length ?: 0) +
        genres.size * 4
    return (sizeChars + 64L) * 2L
}

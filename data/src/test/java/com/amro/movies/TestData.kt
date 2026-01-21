package com.amro.movies

import com.amro.movies.data.remote.resource.GenreResource
import com.amro.movies.data.remote.resource.GenreListResource
import com.amro.movies.data.remote.resource.MovieDetailsResource
import com.amro.movies.data.remote.resource.MovieResource
import com.amro.movies.data.remote.resource.TrendingMoviesResource
import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import com.amro.movies.domain.model.MovieDetails
import java.time.LocalDate

object TestData {

    val genreResource1 = GenreResource(id = 28, name = "Action")
    val genreResource2 = GenreResource(id = 35, name = "Comedy")
    val genreResource3 = GenreResource(id = 18, name = "Drama")

    val genre1 = Genre(id = 28, name = "Action")
    val genre2 = Genre(id = 35, name = "Comedy")
    val genre3 = Genre(id = 18, name = "Drama")

    val genreListResource = GenreListResource(
        genres = listOf(genreResource1, genreResource2, genreResource3)
    )

    val movieResource1 = MovieResource(
        id = 1,
        title = "Test Movie 1",
        posterPath = "/poster1.jpg",
        backdropPath = "/backdrop1.jpg",
        genreIds = listOf(28, 35),
        popularity = 100.0,
        releaseDate = "2024-01-15",
        voteAverage = 7.5,
        voteCount = 1000,
        overview = "Test overview 1"
    )

    val movieResource2 = MovieResource(
        id = 2,
        title = "Another Movie",
        posterPath = "/poster2.jpg",
        backdropPath = "/backdrop2.jpg",
        genreIds = listOf(18),
        popularity = 80.0,
        releaseDate = "2024-02-20",
        voteAverage = 8.0,
        voteCount = 500,
        overview = "Test overview 2"
    )

    val movie1 = Movie(
        id = 1,
        title = "Test Movie 1",
        posterPath = "/poster1.jpg",
        backdropPath = "/backdrop1.jpg",
        overview = "Test overview 1",
        genres = listOf(genre1, genre2),
        popularity = 100.0,
        releaseDate = LocalDate.of(2024, 1, 15),
        voteAverage = 7.5
    )

    val movie2 = Movie(
        id = 2,
        title = "Another Movie",
        posterPath = "/poster2.jpg",
        backdropPath = "/backdrop2.jpg",
        overview = "Test overview 2",
        genres = listOf(genre3),
        popularity = 80.0,
        releaseDate = LocalDate.of(2024, 2, 20),
        voteAverage = 8.0
    )

    val trendingMoviesResource = TrendingMoviesResource(
        page = 1,
        results = listOf(movieResource1, movieResource2),
        totalPages = 1,
        totalResults = 2
    )

    val movieDetailsResource = MovieDetailsResource(
        id = 1,
        title = "Test Movie 1",
        tagline = "A test movie tagline",
        posterPath = "/poster1.jpg",
        backdropPath = "/backdrop1.jpg",
        genres = listOf(genreResource1, genreResource2),
        overview = "This is a test movie overview.",
        voteAverage = 7.5,
        voteCount = 1000,
        budget = 100000000,
        revenue = 500000000,
        status = "Released",
        imdbId = "tt1234567",
        runtime = 120,
        releaseDate = "2024-01-15"
    )

    val movieDetails = MovieDetails(
        id = 1,
        title = "Test Movie 1",
        tagline = "A test movie tagline",
        posterPath = "/poster1.jpg",
        backdropPath = "/backdrop1.jpg",
        genres = listOf(genre1, genre2),
        overview = "This is a test movie overview.",
        voteAverage = 7.5,
        voteCount = 1000,
        budget = 100000000,
        revenue = 500000000,
        status = "Released",
        imdbId = "tt1234567",
        runtime = 120,
        releaseDate = LocalDate.of(2024, 1, 15)
    )
}

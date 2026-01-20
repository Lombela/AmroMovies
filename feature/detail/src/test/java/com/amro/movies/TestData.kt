package com.amro.movies

import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.MovieDetails
import java.time.LocalDate

object TestData {

    private val genre1 = Genre(id = 28, name = "Action")
    private val genre2 = Genre(id = 35, name = "Comedy")

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

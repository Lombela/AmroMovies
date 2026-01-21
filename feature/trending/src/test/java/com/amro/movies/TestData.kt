package com.amro.movies

import com.amro.movies.domain.model.Genre
import com.amro.movies.domain.model.Movie
import java.time.LocalDate

object TestData {

    val genre1 = Genre(id = 28, name = "Action")
    val genre2 = Genre(id = 35, name = "Comedy")
    val genre3 = Genre(id = 18, name = "Drama")

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
}

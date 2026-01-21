package com.amro.movies.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movie_genre",
    primaryKeys = ["movieId", "genreId"],
    indices = [
        Index("movieId"),
        Index("genreId")
    ]
)
data class MovieGenreCrossRef(
    val movieId: Int,
    val genreId: Int
)

package com.amro.movies.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "movie_list_entries",
    primaryKeys = ["userId", "listType", "movieId"],
    indices = [
        Index(value = ["userId", "listType", "position"]),
        Index("movieId")
    ]
)
data class MovieListEntryEntity(
    val userId: String,
    val listType: String,
    val movieId: Int,
    val position: Int,
    val page: Int,
    val fetchedAtEpochMs: Long
)

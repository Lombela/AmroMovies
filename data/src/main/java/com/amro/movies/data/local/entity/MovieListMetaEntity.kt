package com.amro.movies.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "movie_list_meta",
    primaryKeys = ["userId", "listType"]
)
data class MovieListMetaEntity(
    val userId: String,
    val listType: String,
    val nextPage: Int?,
    val lastFetchedEpochMs: Long,
    val totalResults: Int?
)

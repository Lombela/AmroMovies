package com.amro.movies.data.local.model

import androidx.room.Embedded
import androidx.room.Relation
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieListEntryEntity

data class MovieListEntryWithMovie(
    @Embedded val entry: MovieListEntryEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "movieId",
        entity = MovieEntity::class
    )
    val movie: MovieWithGenres
)

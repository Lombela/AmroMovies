package com.amro.movies.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef

data class MovieWithGenres(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "genreId",
        associateBy = Junction(MovieGenreCrossRef::class)
    )
    val genres: List<GenreEntity>
)

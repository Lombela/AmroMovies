package com.amro.movies.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amro.movies.data.local.entity.GenreEntity
import com.amro.movies.data.local.entity.MovieEntity
import com.amro.movies.data.local.entity.MovieGenreCrossRef
import com.amro.movies.data.local.entity.MovieListEntryEntity
import com.amro.movies.data.local.entity.MovieListMetaEntity

@Database(
    entities = [
        MovieEntity::class,
        GenreEntity::class,
        MovieGenreCrossRef::class,
        MovieListEntryEntity::class,
        MovieListMetaEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}

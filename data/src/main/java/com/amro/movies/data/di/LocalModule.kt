package com.amro.movies.data.di

import android.content.Context
import androidx.room.Room
import com.amro.movies.data.local.db.MovieDao
import com.amro.movies.data.local.db.MoviesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideMoviesDatabase(@ApplicationContext context: Context): MoviesDatabase {
        return Room.databaseBuilder(
            context,
            MoviesDatabase::class.java,
            "movies.db"
        ).build()
    }

    @Provides
    fun provideMovieDao(database: MoviesDatabase): MovieDao {
        return database.movieDao()
    }
}

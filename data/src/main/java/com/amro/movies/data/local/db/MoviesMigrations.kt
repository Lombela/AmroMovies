package com.amro.movies.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MoviesMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `movie_details` (
                    `movieId` INTEGER NOT NULL,
                    `title` TEXT NOT NULL,
                    `tagline` TEXT,
                    `posterPath` TEXT,
                    `backdropPath` TEXT,
                    `overview` TEXT,
                    `voteAverage` REAL NOT NULL,
                    `voteCount` INTEGER NOT NULL,
                    `budget` INTEGER NOT NULL,
                    `revenue` INTEGER NOT NULL,
                    `status` TEXT,
                    `imdbId` TEXT,
                    `runtime` INTEGER,
                    `releaseDate` TEXT,
                    `lastUpdatedEpochMs` INTEGER NOT NULL,
                    PRIMARY KEY(`movieId`)
                )
                """.trimIndent()
            )
        }
    }
}

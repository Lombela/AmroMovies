package com.amro.movies.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amro.movies.core.util.Constants
import com.amro.movies.domain.model.MovieListType
import com.amro.movies.domain.repository.MovieRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.amro.movies.core.util.Result as DataResult

@HiltWorker
class NightlyMovieSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val movieRepository: MovieRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString(MovieSyncWork.KEY_USER_ID) ?: Constants.DEFAULT_USER_ID
        val trending = movieRepository.refreshMovieList(userId, MovieListType.TRENDING)
        val popular = movieRepository.refreshMovieList(userId, MovieListType.POPULAR)

        return if (trending is DataResult.Success && popular is DataResult.Success) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}

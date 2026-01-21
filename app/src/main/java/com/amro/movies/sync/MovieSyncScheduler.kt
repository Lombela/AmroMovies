package com.amro.movies.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.amro.movies.core.util.Constants
import com.amro.movies.data.work.MovieSyncWork
import com.amro.movies.data.work.NightlyMovieSyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieSyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun enqueueNightlySync(userId: String = Constants.DEFAULT_USER_ID) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val initialDelayMs = calculateInitialDelayMs(targetHour = 2)
        val request = PeriodicWorkRequestBuilder<NightlyMovieSyncWorker>(
            1,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES)
            .setInputData(workDataOf(MovieSyncWork.KEY_USER_ID to userId))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MovieSyncWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateInitialDelayMs(targetHour: Int): Long {
        val now = Calendar.getInstance()
        val nextRun = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return nextRun.timeInMillis - now.timeInMillis
    }
}

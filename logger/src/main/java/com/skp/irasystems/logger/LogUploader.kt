package com.skp.irasystems.logger

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

private val isUploading = AtomicBoolean(false)

@HiltWorker
internal class LogUploader @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val ioDispatcher: CoroutineDispatcher,
    private val persistService: LogPersistService,
    private val uploadService: LogUploadService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if (!isUploading.compareAndSet(false, true)) {
            return Result.success() // avoid duplicate uploads
        }
        return try {
            withContext(ioDispatcher) {
                val logs = persistService.getPendingLogs()
                if (logs.isEmpty()) return@withContext Result.success()
                return@withContext if (uploadService.uploadLogs(logs)) {
                    persistService.markUploaded(logs.map { it.id })
                    persistService.purgeUploadedLogs()
                    Result.success()
                } else Result.retry()
            }
        } finally {
            isUploading.set(false)
        }
    }
}

internal fun scheduleLogUpload(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val request = PeriodicWorkRequestBuilder<LogUploader>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "LogUploader",
        ExistingPeriodicWorkPolicy.KEEP,
        request
    )
}

internal fun uploadLogs(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val request = OneTimeWorkRequestBuilder<LogUploader>()
        .setConstraints(constraints)
        .build()
    WorkManager.getInstance(context).enqueue(request)
}

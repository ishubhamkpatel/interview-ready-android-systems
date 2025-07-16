package com.skp.irasystems.logger

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

interface Logger {
    fun log(message: String)
    fun schedule()
    fun resumeOnBoot()
}

internal class LoggerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scope: CoroutineScope,
    private val persistService: LogPersistService
): Logger {
    companion object {
        private const val LOGS_THRESHOLD = 50
    }

    init {
        scope.launch {
            persistService.observePendingLogsCount().collect {
                if (it >= LOGS_THRESHOLD) {
                    uploadLogs(context)
                }
            }
        }
    }

    override fun log(message: String) {
        scope.launch { persistService.log(message) }
    }

    override fun schedule() = scheduleLogUpload(context)

    override fun resumeOnBoot() = uploadLogs(context)
}

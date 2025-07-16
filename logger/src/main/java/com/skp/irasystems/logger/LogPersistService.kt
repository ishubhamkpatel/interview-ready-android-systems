package com.skp.irasystems.logger

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal interface LogPersistService {
    suspend fun log(message: String)
    fun observePendingLogsCount(): Flow<Int>
    suspend fun getPendingLogs(): List<LogEntry>
    suspend fun markUploaded(ids: List<Long>)
    suspend fun purgeUploadedLogs()
}

internal class LogPersistServiceImpl @Inject constructor(
    private val logDao: LogDao
) : LogPersistService {
    override suspend fun log(message: String) {
        logDao.insert(LogEntity(message = message, timestamp = System.currentTimeMillis()))
    }

    override fun observePendingLogsCount(): Flow<Int> {
        return logDao.observePendingLogsCount()
    }

    override suspend fun getPendingLogs(): List<LogEntry> =
        logDao.getPendingLogs().map {
            LogEntry(it.id, it.message, it.timestamp, it.uploaded)
        }

    override suspend fun markUploaded(ids: List<Long>) = logDao.markAsUploaded(ids)

    override suspend fun purgeUploadedLogs() = logDao.purgeUploadedLogs()
}

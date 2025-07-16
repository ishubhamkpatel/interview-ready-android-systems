package com.skp.irasystems.logger

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

internal interface LogApi {
    @POST("upload-logs")
    suspend fun uploadLogs(@Body logs: List<LogEntry>): Response<Unit>
}

internal interface LogUploadService {
    suspend fun uploadLogs(logs: List<LogEntry>): Boolean
}

internal class LogUploadServiceImpl @Inject constructor(
    private val logApi: LogApi
) : LogUploadService {
    override suspend fun uploadLogs(logs: List<LogEntry>): Boolean {
        return try {
            // can be wrapped with flow for backoff retries
            val response = logApi.uploadLogs(logs)
            // handle for empty or error responses and different HTTP status codes
            response.isSuccessful
        } catch (e: Exception) {
            // handle IO, `OkHttp`, etc., and other exceptions
            false
        }
    }
}

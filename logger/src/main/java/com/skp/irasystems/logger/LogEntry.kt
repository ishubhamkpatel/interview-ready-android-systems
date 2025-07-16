package com.skp.irasystems.logger

data class LogEntry(
    val id: Long = 0,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val uploaded: Boolean = false
)

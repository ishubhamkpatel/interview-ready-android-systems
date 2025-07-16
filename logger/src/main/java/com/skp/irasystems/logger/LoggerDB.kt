package com.skp.irasystems.logger

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

internal const val LOGS_TABLE = "logs"

@Entity(tableName = LOGS_TABLE)
internal data class LogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val timestamp: Long,
    val uploaded: Boolean = false
)

@Dao
internal interface LogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: LogEntity)

    @Query("SELECT COUNT(*) FROM logs WHERE uploaded = 0")
    fun observePendingLogsCount(): Flow<Int>

    @Query("SELECT * FROM $LOGS_TABLE WHERE uploaded = 0")
    suspend fun getPendingLogs(): List<LogEntity>

    @Query("UPDATE $LOGS_TABLE SET uploaded = 1 WHERE id IN (:ids)")
    suspend fun markAsUploaded(ids: List<Long>)

    @Query("DELETE FROM $LOGS_TABLE WHERE uploaded = 1")
    suspend fun purgeUploadedLogs()
}

@Database(entities = [LogEntity::class], version = 1)
internal abstract class LoggerDatabase : RoomDatabase() {
    abstract fun logDao(): LogDao
}

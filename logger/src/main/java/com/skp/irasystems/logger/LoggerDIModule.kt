package com.skp.irasystems.logger

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LoggerDIModule {
    companion object {
        @Provides
        @Singleton
        fun provideLoggerDatabase(@ApplicationContext context: Context): LoggerDatabase =
            Room.databaseBuilder(context, LoggerDatabase::class.java, "$LOGS_TABLE.db").build()

        @Provides
        @Reusable
        fun provideLogDao(db: LoggerDatabase): LogDao = db.logDao()

        @Provides
        @Singleton
        fun provideRetrofit(converterFactory: Converter.Factory): Retrofit = Retrofit.Builder()
            .baseUrl("https://api-myloggerapp.com/v1/") // dummy URL
            .addConverterFactory(converterFactory)
            .build()

        @Provides
        @Reusable
        fun provideMoshiConverterFactory(): Converter.Factory = MoshiConverterFactory.create()

        @Provides
        @Singleton
        fun provideLogApi(retrofit: Retrofit): LogApi = retrofit.create()

        @Provides
        @Singleton
        fun provideCoroutineScope(ioDispatcher: CoroutineDispatcher): CoroutineScope =
            CoroutineScope(SupervisorJob() + ioDispatcher)

        @Provides
        fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }

    @Binds
    @Singleton
    abstract fun bindLogger(impl: LoggerImpl): Logger

    @Binds
    @Singleton
    abstract fun bindLogPersistService(impl: LogPersistServiceImpl): LogPersistService

    @Binds
    @Singleton
    abstract fun bindLogUploadService(impl: LogUploadServiceImpl): LogUploadService
}

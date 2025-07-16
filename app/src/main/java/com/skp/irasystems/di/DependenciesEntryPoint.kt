package com.skp.irasystems.di

import com.skp.irasystems.logger.Logger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DependenciesEntryPoint {
    fun logger(): Logger
}
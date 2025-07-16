package com.skp.irasystems

import android.app.Application
import com.skp.irasystems.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApp: Application() {
    @Inject
    private lateinit var logger: Logger

    override fun onCreate() {
        super.onCreate()
        logger.schedule()
    }
}
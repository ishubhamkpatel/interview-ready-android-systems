package com.skp.irasystems.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skp.irasystems.di.DependenciesEntryPoint
import com.skp.irasystems.logger.Logger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class BootCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                DependenciesEntryPoint::class.java
            )
            entryPoint.logger().resumeOnBoot()
        }
    }
}
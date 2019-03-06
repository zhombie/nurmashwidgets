package org.nurmash.lib.nurmashwidgets.customtabs

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Empty service to bind to, raising the application's importance.
 */
internal class KeepAliveService : Service() {

    companion object {
        private val binder = Binder()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

}
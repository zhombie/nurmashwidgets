package org.nurmash.lib.sample

import android.app.Application
import org.nurmash.lib.nurmashwidgets.customtabs.CustomTabsActivityLifecycleCallbacks

/**
 * Optional pre-loading for improved performance
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        // Preload custom tabs service for improved performance
        // This is optional but recommended
        registerActivityLifecycleCallbacks(CustomTabsActivityLifecycleCallbacks())
    }
}
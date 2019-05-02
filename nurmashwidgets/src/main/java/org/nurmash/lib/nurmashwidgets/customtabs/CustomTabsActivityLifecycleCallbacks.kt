package org.nurmash.lib.nurmashwidgets.customtabs

import android.app.Activity
import android.app.Application
import android.os.Bundle

class CustomTabsActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private var browser: Browser? = null

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        browser = Browser()
    }

    override fun onActivityStarted(activity: Activity?) {}

    override fun onActivityResumed(activity: Activity?) {
        activity?.let {
            browser?.bindCustomTabsService(it)
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.let {
            browser?.unbindCustomTabsService(it)
        }
    }

    override fun onActivityStopped(activity: Activity?) {}

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity?) {}
}
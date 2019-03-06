package org.nurmash.lib.nurmashwidgets.customtabs

import android.app.Activity
import android.app.Application
import android.os.Bundle

class CustomTabsActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    private var customTabsHelper: CustomTabsHelper? = null

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        customTabsHelper = CustomTabsHelper()
    }

    override fun onActivityStarted(activity: Activity?) {}

    override fun onActivityResumed(activity: Activity?) {
        activity?.let {
            customTabsHelper?.bindCustomTabsService(it)
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        activity?.let {
            customTabsHelper?.unbindCustomTabsService(it)
        }
    }

    override fun onActivityStopped(activity: Activity?) {}

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}

    override fun onActivityDestroyed(activity: Activity?) {}
}
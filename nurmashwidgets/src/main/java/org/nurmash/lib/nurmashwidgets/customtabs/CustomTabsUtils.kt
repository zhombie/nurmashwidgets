package org.nurmash.lib.nurmashwidgets.customtabs

import androidx.browser.customtabs.CustomTabsIntent

val defaultCustomTabsIntentBuilder: CustomTabsIntent.Builder
    get() = CustomTabsIntent.Builder()
        .addDefaultShareMenuItem()
        .setShowTitle(true)

val defaultCustomTabsIntent: CustomTabsIntent
    get() = defaultCustomTabsIntentBuilder.build()
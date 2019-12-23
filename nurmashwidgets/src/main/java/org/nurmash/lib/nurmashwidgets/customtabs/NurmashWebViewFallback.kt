package org.nurmash.lib.nurmashwidgets.customtabs

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Default [Browser.CustomTabFallback] implementation
 * that uses [NurmashWebViewActivity] to display the requested [Uri].
 */
class NurmashWebViewFallback : Browser.CustomTabFallback {

    /**
     * @param context The [Context] that wants to open the Uri
     * @param uri     The [Uri] to be opened by the fallback
     */
    override fun openUri(context: Context, uri: Uri) {
        val intent = Intent(context, NurmashWebViewActivity::class.java)
        intent.putExtra(NurmashWebViewActivity.EXTRA_URL, uri.toString())
        context.startActivity(intent)
    }

}
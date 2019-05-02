package org.nurmash.lib.nurmashwidgets.customtabs

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Default [Browser.CustomTabFallback] implementation
 * that uses [WebViewActivity] to display the requested [Uri].
 */
class WebViewFallback : Browser.CustomTabFallback {

    /**
     * @param context The [Context] that wants to open the Uri
     * @param uri     The [Uri] to be opened by the fallback
     */
    override fun openUri(context: Context, uri: Uri) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_URL, uri.toString())
        context.startActivity(intent)
    }

}
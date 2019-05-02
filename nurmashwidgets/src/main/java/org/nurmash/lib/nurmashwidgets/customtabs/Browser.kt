package org.nurmash.lib.nurmashwidgets.customtabs

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import com.google.android.material.snackbar.Snackbar
import org.nurmash.lib.nurmashwidgets.R

@Suppress("unused")
class Browser {

    companion object {
        private const val TAG = "Browser"

        private const val EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE"

        /**
         * Opens the URL on a Custom Tab if possible. Otherwise fallbacks to opening it on a WebView
         *
         * @param context          The host activity
         * @param customTabsIntent a CustomTabsIntent to be used if Custom Tabs is available
         * @param url              the url to be opened
         * @param fallback         a CustomTabFallback to be used if Custom Tabs is not available
         */
        fun openLink(context: Context,
                     customTabsIntent: CustomTabsIntent,
                     url: String,
                     fallback: CustomTabFallback? = WebViewFallback(),
                     view: View? = null,
                     errorText: String? = null,
                     @StringRes errorTextRes: Int? = null
        ) {
            if (Patterns.WEB_URL.matcher(url).matches()) {
                val uri = Uri.parse(url)
                val packageName = CustomTabsPackageHelper.getPackageNameToUse(context)

                // If we cant find a package name, it means there's no browser that supports
                // Chrome Custom Tabs installed. So, we fallback to the webView
                if (packageName == null) {
                    fallback?.openUri(context = context, uri = uri)
                } else {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            customTabsIntent.intent.putExtra(
                                Intent.EXTRA_REFERRER,
                                Uri.parse(Intent.URI_ANDROID_APP_SCHEME.toString() + "//" + context.packageName)
                            )
                        }

                        customTabsIntent.intent.setPackage(packageName)
                        customTabsIntent.launchUrl(context, uri)
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                    }
                }
            } else {
                val text = when {
                    errorText != null -> {
                        if (errorTextRes != null) {
                            Log.e(TAG, "Please specify just one error text")
                        }
                        errorText
                    }
                    errorTextRes != null -> context.getString(errorTextRes)
                    else -> context.getString(R.string.error_message_link_broken)
                }

                if (view == null) {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        fun addKeepAliveExtra(context: Context, intent: Intent) {
            val keepAliveIntent = Intent().setClassName(
                context.packageName,
                KeepAliveService::class.java.canonicalName ?: ""
            )
            intent.putExtra(EXTRA_CUSTOM_TABS_KEEP_ALIVE, keepAliveIntent)
        }
    }

    private var customTabsSession: CustomTabsSession? = null
    private var client: CustomTabsClient? = null
    private var connection: CustomTabsServiceConnection? = null
    private var connectionCallback: ConnectionCallback? = null

    /**
     * Creates or retrieves an exiting CustomTabsSession
     *
     * @return a CustomTabsSession
     */
    val session: CustomTabsSession?
        get() {
            if (client == null) {
                customTabsSession = null
            } else if (customTabsSession == null) {
                customTabsSession = client?.newSession(null)
            }
            return customTabsSession
        }

    /**
     * Unbinds the Activity from the Custom Tabs Service
     *
     * @param activity the activity that is connected to the service
     */
    fun unbindCustomTabsService(activity: Activity) {
        if (connection == null) {
            return
        }
        activity.unbindService(connection)
        client = null
        customTabsSession = null
    }

    /**
     * Register a Callback to be called when connected or disconnected from the Custom Tabs Service
     */
    fun setConnectionCallback(connectionCallback: ConnectionCallback) {
        this@Browser.connectionCallback = connectionCallback
    }

    /**
     * Binds the Activity to the Custom Tabs Service
     *
     * @param activity the activity to be bound to the service
     */
    fun bindCustomTabsService(activity: Activity) {
        if (client != null) {
            return
        }

        val packageName = CustomTabsPackageHelper.getPackageNameToUse(activity) ?: return

        connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                this@Browser.client = client
                this@Browser.client?.warmup(0L)
                connectionCallback?.onCustomTabsConnected()
                //Initialize a session as soon as possible.
                session
            }

            override fun onServiceDisconnected(name: ComponentName) {
                client = null
                connectionCallback?.onCustomTabsDisconnected()
            }

            override fun onBindingDied(name: ComponentName) {
                client = null
                connectionCallback?.onCustomTabsDisconnected()
            }
        }

        CustomTabsClient.bindCustomTabsService(activity, packageName, connection)
    }

    fun mayLaunchUrl(uri: Uri, extras: Bundle?, otherLikelyBundles: List<Bundle>?): Boolean {
        if (client == null) {
            return false
        }
        val session = session
        return session != null && session.mayLaunchUrl(uri, extras, otherLikelyBundles)
    }

    /**
     * A Callback for when the service is connected or disconnected. Use those callbacks to
     * handle UI changes when the service is connected or disconnected
     */
    interface ConnectionCallback {
        /**
         * Called when the service is connected
         */
        fun onCustomTabsConnected()

        /**
         * Called when the service is disconnected
         */
        fun onCustomTabsDisconnected()
    }

    /**
     * To be used as a fallback to open the Uri when Custom Tabs is not available
     */
    interface CustomTabFallback {
        /**
         * @param context The Activity that wants to open the Uri
         * @param uri     The uri to be opened by the fallback
         */
        fun openUri(context: Context, uri: Uri)
    }

}
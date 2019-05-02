package org.nurmash.lib.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.nurmash.lib.nurmashwidgets.MultiStateView
import org.nurmash.lib.nurmashwidgets.customtabs.Browser
import org.nurmash.lib.nurmashwidgets.customtabs.WebViewFallback

class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL = "https://www.google.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val names = listOf("Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan",
            "Aachen", "Aarhus", "Aba", "Abadan", "Abaetetuba", "Abakan", "Abbotabad", "Abbotsford", "Abeokuta", "Aberdeen", "Abha", "Abidjan")
        val adapter  = ArrayAdapter <String>(
            this,
            android.R.layout.simple_list_item_1, names
        )
        listView.adapter = adapter

        //customizing
        multiStateView.setEmptyAltText(R.string.empty_alt_text)
        multiStateView.setErrorAltText(R.string.error_alt_text)

        multiStateView.setEmptyStateActionListener{
            Toast.makeText(this@MainActivity, "Empty state action", Toast.LENGTH_SHORT).show()
        }

        multiStateView.setErrorStateActionListener{
            Toast.makeText(this@MainActivity, "Error state action", Toast.LENGTH_SHORT).show()
        }


        fab.setOnClickListener {
            when (multiStateView.getViewState()){
               is MultiStateView.Companion.State.Unknown -> multiStateView.showContent()
               is MultiStateView.Companion.State.Content -> multiStateView.showEmptyView()
               is MultiStateView.Companion.State.Error -> multiStateView.showContent()
               is MultiStateView.Companion.State.Empty -> multiStateView.showErrorView()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Thread {
            multiStateView.post { multiStateView.showLoading() }
            Thread.sleep(3000)
            runOnUiThread { multiStateView.showContent() }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private val defaultCustomTabsIntentBuilder: CustomTabsIntent.Builder
        get() = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setShowTitle(true)

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.load_url -> {
                val customTabsIntent = defaultCustomTabsIntentBuilder.build()
                Browser.addKeepAliveExtra(this, customTabsIntent.intent)
                Browser.openLink(
                    context = this@MainActivity,
                    view = contentView,
                    errorText = "sdg",
                    errorTextRes = R.string.error_alt_text,
                    customTabsIntent = customTabsIntent,
                    url = URL,
                    fallback = WebViewFallback()
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}

package org.nurmash.lib.sample

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.nurmash.lib.nurmashwidgets.MultiStateView


class MainActivity : AppCompatActivity() {

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

}

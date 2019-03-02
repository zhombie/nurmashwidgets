package org.nurmash.lib.sample

import android.os.Bundle
import android.widget.ArrayAdapter
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



        fab.setOnClickListener {
            when (multiStateView.getViewState()){
                MultiStateView.Companion.State.Unknown -> multiStateView.showContent()
                MultiStateView.Companion.State.Content -> multiStateView.showEmptyView()
                MultiStateView.Companion.State.Error -> multiStateView.showContent()
                MultiStateView.Companion.State.Empty -> multiStateView.showErrorView()
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

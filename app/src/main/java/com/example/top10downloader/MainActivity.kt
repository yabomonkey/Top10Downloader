package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageURL: String = ""
}

private const val TAG = "MainActivity"
private const val STATE_URL = "FeedUrl"
private const val STATE_LIMIT = "FeedLimit"
private const val STATE_SAVED = "saved"

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var xmlListView: ListView

    private var feedUrl: String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        xmlListView = findViewById(R.id.xmlListView)

        downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate done")
    }

    private fun downloadUrl(feedUrl: String){
        if(feedUrl != feedCachedURL) {
            Log.d(TAG, "downloadUrl starting AsyncTask")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)
            feedCachedURL = feedUrl
            Log.d(TAG, "downloadUrl Done")
        } else {
            Log.d(TAG, "downloadURL - URL not changed")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId){
            R.id.mnuFree ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            R.id.mnuPaid ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            R.id.mnuSongs ->
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            R.id.mnu10, R.id.mnu25 -> {
                if(!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected ${item.title} setting feedLimit to $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected ${item.title} setting feedLimit unchanged")
                }
            }
            R.id.mnuRefresh -> feedCachedURL = "INVALIDATED"
            else ->
                return super.onOptionsItemSelected(item)

        }

        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(feedUrl != null && feedLimit != null){
            outState.putString(STATE_URL, feedUrl)
            outState.putInt(STATE_LIMIT, feedLimit)
            outState.putBoolean(STATE_SAVED, true)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState.getBoolean(STATE_SAVED, false)){
            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }
        downloadUrl(feedUrl.format(feedLimit))

    }


}





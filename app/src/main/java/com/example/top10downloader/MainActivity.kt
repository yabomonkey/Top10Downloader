package com.example.top10downloader

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called")
        val downloadData = DownloadData()
        downloadData.execute("URL goes here")
        Log.d(TAG, "onCreate Done")
    }

    companion object {
        private class DownloadData : AsyncTask<String, Void, String>(){
            private val TAG = "DownloadData"

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute: parameter is $result")
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed = downloadXML(url[0])
                if( rssFeed.isEmpty()){
                    Log.e(TAG, "doInBackground: error downloading")
                }
                return rssFeed
            }
        }
    }

    private fun downloadXML(urlPath: String?): String{
        val xmlResult = StringBuilder()

        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            Log.d(TAG, "downloadXML: The response code was $response")

//            val inputStream = connection.inputStream
//            val inputStreamReader = InputStreamReader(inputStream)
//            val reader = BufferedReader(inputStreamReader)
            val reader = BufferedReader(InputStreamReader(connection.inputStream))

            val inputBuffer = charArrayOf(500)
            var charsRead = 0
            while (charsRead >= 0){
                charsRead = reader.read(inputBuffer)
                if (charsRead > 0){
                    xmlResult.append(String(inputBuffer, 0 , charsRead))
                } catch (e: MalformedURLException) {
                    Log.e(TAG, "downloadURL: Invalid URL ${e.message}")
                } catch (e: IOException) {
                    Log.e(TAG, "downloadURL: IO Exception reading data ${e.message}")
                } catch (e: SecurityException) {
                    Log.e(TAG, "downloadURL: Security Exception. Need permissions? ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "Unknown Error: ${e.message}")
                }
            }

        } catch (e: MalformedURLException){
            Log.e(TAG, "downloadURL: Invalid URL ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "downloadURL: IO Exception reading data ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unknown Error: ${e.message}")
        }
    }
}


package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class ParseApplications {
    private val TAG = "ParseApplications"
    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var gotImage = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.lowercase()
                when (eventType){
                    XmlPullParser.START_TAG -> {
//                        Log.d(TAG, "parse: Starting tag for " + tagName)
                        if (tagName == "entry"){
                            inEntry = true
                        } else if ((tagName == "image") && inEntry) {
                            val imageResolution = xpp.getAttributeValue(null, "height")
                            if (imageResolution.isNotEmpty()){
                                gotImage = imageResolution == "53"
                            }
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
//                        Log.d(TAG, "parse: ending tag for " + tagName)
                        if(inEntry) {
                            when(tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry() //create a new object
                                }

                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> if(gotImage) currentRecord.imageURL = textValue
                            }
                        }
                    }

                }

                eventType = xpp.next()
            }

//            for(app in applications) {
//                Log.d(TAG, "*************")
//                Log.d(TAG, app.toString())
//            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }

        return status
    }
}
package com.example.bookseeker.adapter

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.example.bookseeker.model.data.Recommend
import com.google.gson.GsonBuilder
import org.json.JSONArray
import java.io.IOException
import java.nio.charset.Charset

object Utils {
    private val TAG = "Utils"

    fun loadRecommendData(context: Context): List<Recommend> {
        try {
            val builder = GsonBuilder()
            val gson = builder.create()
            val array = JSONArray(loadJSONFromAsset(context, "bookdata.json"))
            val recommendDataList = ArrayList<Recommend>()
            for (i in 0 until array.length()) {
                val recommendData = gson.fromJson(array.getString(i), Recommend::class.java)
                recommendDataList.add(recommendData)
            }
            return recommendDataList
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }

    }

    private fun loadJSONFromAsset(context: Context, jsonFileName: String): String {
        try {
            val manager = context.assets
            Log.d(TAG, "path $jsonFileName")
            val inputStream = manager.open(jsonFileName)
            val size = inputStream!!.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            return String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return "{}"
        }
    }

    fun getDisplaySize(windowManager: WindowManager): Point {
        try {
            if (Build.VERSION.SDK_INT > 16) {
                val display = windowManager.defaultDisplay
                val displayMetrics = DisplayMetrics()
                display.getMetrics(displayMetrics)
                return Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
            } else {
                return Point(0, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Point(0, 0)
        }

    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
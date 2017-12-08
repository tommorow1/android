package com.example.bloold.buildp.presenters

import android.os.AsyncTask
import android.util.Log
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by bloold on 29.10.17.
 */
interface callback{
    fun onObjectsLoaded(items: ArrayList<SortObject>)
    fun onSortedObjectsLoaded(items: ArrayList<SortObject>)
}

class SortPresenter(private val view: callback) : AsyncTask<String, String, ArrayList<SortObject>>() {

    override fun onPreExecute() {
        super.onPreExecute()
        //dialog.show()
    }

    override fun doInBackground(vararg params: String): ArrayList<SortObject>? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        try {
            val url = URL(params[0])
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Device-Id", Settings.getUdid())

            val header = "Basic " + String(android.util.Base64.encode("defa:defa".toByteArray(), android.util.Base64.NO_WRAP))
            connection.addRequestProperty("Authorization", header)
            connection.connect()

            val stream = connection.inputStream
            reader = BufferedReader(InputStreamReader(stream))

            val buffer = StringBuffer()
            var line: String? = ""

            line = reader.readLine()

            while (line != null && line != "") {
                buffer.append(line)
                line = reader.readLine()
            }

            val finalJson = JSONObject(buffer.toString())
            Log.d("json", finalJson.toString())

            try {
                return getSortObjects(finalJson.getJSONArray("DATA"))
            } catch (e: Exception){
                e.printStackTrace()
            }

            return null
        } catch (e: MalformedURLException) {
            //e.printStackTrace()
        } catch (e: IOException) {
            //e.printStackTrace()
        } catch (e: JSONException) {
            //e.printStackTrace()
        } finally {
            if (connection != null) {
                connection.disconnect()
            }
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (e: IOException) {
                //e.printStackTrace()
            }

        }
        return null
    }

    override fun onPostExecute(result: ArrayList<SortObject>?) {
        super.onPostExecute(result)
        //dialog.dismiss()
        if (result != null) {
            view.onSortedObjectsLoaded(result)
            Log.d("result", "onPostExecute")
        } else {
            //Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    fun getSortObjects(responseArr: JSONArray?): ArrayList<SortObject>? {

        var finalObjects = ArrayList<SortObject>()

        for(i: Int in 0 until (responseArr?.length()?:0)){
            var currentObject = responseArr?.getJSONObject(i)

            val sortModel = SortObject()

            try {
                sortModel.id = currentObject?.getString("ID")
                sortModel.name = currentObject?.getString("NAME")
            } catch (e: JSONException) {
                //e.printStackTrace()
            }

            //CHILD

            try {
                val childsJsonArray = currentObject?.getJSONArray("CHILD")
                sortModel.child = getSortObjects(childsJsonArray)?.toTypedArray()
                Log.d("childs", childsJsonArray.toString())
            } catch (e: JSONException) {
                //e.printStackTrace()
            }

            finalObjects.add(sortModel)
        }

        return finalObjects
    }
}
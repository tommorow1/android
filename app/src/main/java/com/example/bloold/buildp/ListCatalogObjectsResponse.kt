package com.example.bloold.buildp

import android.os.AsyncTask
import android.util.Log
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.model.*
import com.google.gson.Gson
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
 * Created by bloold on 21.10.17.
 */
interface callback{
    fun onObjectsLoaded(items: ArrayList<CatalogObjectsModel>)
    fun onFiltersLoaded(items: ArrayList<HightFilterModelLevel>)
}

class ListCatalogObjectsResponse(private val view: callback) : AsyncTask<String, String, ArrayList<CatalogObjectsModel>>() {
    private var searchType = ""
    override fun onPreExecute() {
        super.onPreExecute()
        //dialog.show()
    }

    override fun doInBackground(vararg params: String): ArrayList<CatalogObjectsModel>? {
        var connection: HttpURLConnection? = null
        var reader: BufferedReader? = null

        Log.d("doinback", params[0])

        try {
            val url = URL(params[0]+"&limit=70&page=1")
            if(params.size==2) {
                searchType = params[1]
            }
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

            val finalJson = buffer.toString()
            Log.d("json", finalJson)
            var objects = ArrayList<CatalogObjectsModel>()

            try {
                objects = getCatalogObject(finalJson)
            } catch (e: Exception){
                e.printStackTrace()
            }

            return objects

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

    override fun onPostExecute(result: ArrayList<CatalogObjectsModel>?) {
        super.onPostExecute(result)
        //dialog.dismiss()
        if(result != null) {
            view.onObjectsLoaded(result)
        }
    }

    fun getCatalogObject(responseStr: String): ArrayList<CatalogObjectsModel> {

        val response = JSONObject(responseStr)

        var ITEMS: JSONArray? = null
        var DATA: JSONObject? = null

        val parentArray: JSONArray? = null

        try {
            DATA = JSONObject(response.getString("DATA"))
        } catch (e: JSONException) {
            //e.printStackTrace()
        }

        try {
            ITEMS = JSONArray(DATA!!.getString("ITEMS"))
            //parentArray = DATA.getJSONArray("ITEMS");
        } catch (e: JSONException) {
            //e.printStackTrace()
        }

        val catalogModelList = ArrayList<CatalogObjectsModel>()

        val gson = Gson()
        val array: JSONArray? = null

        for(i:Int in 0..(ITEMS?.length() ?: 1) - 1) {
            val finalObject = ITEMS!!.getJSONObject(i)
            Log.d("i: ${i}", finalObject.toString())
            val catalogModel = CatalogObjectsModel()

            try {
                catalogModel.id = finalObject.getString("ID")
                if (searchType!="") {
                    if(searchType.equals("egrn")){
                        catalogModel.name = finalObject.getString("PROPERTY_EGRKN_NUMBER")+", "+finalObject.getString("NAME")
                    }
                    if(searchType.equals("address")){
                        catalogModel.name = finalObject.getString("PROPERTY_ADDRESS")+", "+finalObject.getString("NAME")
                    }
                    if(searchType.equals("object")){
                        catalogModel.name = finalObject.getString("NAME")
                    }
                }else{
                    catalogModel.name = finalObject.getString("NAME")
                }
                val ImgObject: JSONObject? = finalObject.getJSONObject("DETAIL_PICTURE")

                if (ImgObject != null)
                    catalogModel.src = "http://ruinnet.idefa.ru/" + ImgObject.getString("SRC")

                catalogModel.preview_text = finalObject.getString("PREVIEW_TEXT")

                Log.d("tag", catalogModel.preview_text)

                catalogModel.property_address = finalObject.getString("PROPERTY_ADDRESS")


                //catalogModel.audios = finalObject.getJSONArray("AUDIO_DATA").toString()

                //catalogModel.docs = finalObject.getJSONArray("DOCS_DATA")
                //catalogModel.publications = finalObject.getJSONArray("PUBLICATIONS_DATA")
                //catalogModel.videos = finalObject.getJSONArray("VIDEO_DATA")
                catalogModel.isFavorite = finalObject.getBoolean("IS_FAVORITE")
            } catch (e: JSONException) {

            }

            try {
                //PHOTO
                var photos = ArrayList<PhotoModel>()

                val photosJsonArray = finalObject.getJSONArray("PHOTOS_DATA")

                for (i: Int in 0..photosJsonArray.length() - 1) {
                    val photo = PhotoModel()
                    photo.name = photosJsonArray.getJSONObject(i).getString("NAME")
                    photo.src = "http://ruinnet.idefa.ru/" +
                            photosJsonArray.getJSONObject(i)
                                    .getJSONObject("DETAIL_PICTURE")
                                    .getString("SRC")
                    if (!photo.src.isNullOrEmpty()) {
                        photos.add(photo)
                    }
                }

                catalogModel.photos = photos

                //VIDEO
                var videos = ArrayList<VideoModel>()

                val videosJsonArray = finalObject.getJSONArray("VIDEO_DATA")

                for(i: Int in 0..videosJsonArray.length() - 1){
                    val video = VideoModel()
                    video.name = videosJsonArray.getJSONObject(i).getString("NAME")
                    video.youtubeCode = videosJsonArray.getJSONObject(i).getString("CODE")

                    if(!video.youtubeCode.isNullOrEmpty()){
                        videos.add(video)
                    }
                }

                catalogModel.videos = videos.toTypedArray()

                //AUDIO
                var audios = ArrayList<AudioModel>()

                val audiosJsonArray = finalObject.getJSONArray("AUDIO_DATA")

                for(i: Int in 0..audiosJsonArray.length() - 1){
                    val audio = AudioModel()
                    audio.name = audiosJsonArray .getJSONObject(i).getString("NAME")
                    audio.src = "http://ruinnet.idefa.ru/" +
                            audiosJsonArray .getJSONObject(i).getString("SRC")
                    if(!audio.src.isNullOrEmpty()){
                        audios.add(audio)
                    }
                }

                catalogModel.audios = audios.toTypedArray()

                //DOCS
                var docs = ArrayList<DocModel>()

                val docsJsonArray = finalObject.getJSONArray("AUDIO_DATA")

                for(i: Int in 0..docsJsonArray.length() - 1){
                    val doc = DocModel()
                    doc.name = docsJsonArray.getJSONObject(i).getString("NAME")
                    doc.code = "http://ruinnet.idefa.ru/" +
                            docsJsonArray.getJSONObject(i).getString("SRC")
                    if(!doc.code.isNullOrEmpty()){
                        docs.add(doc)
                    }
                }

                catalogModel.docs = docs.toTypedArray()

            } catch (e: JSONException){
                //e.printStackTrace()
            }

            Log.d("i: ${i}", catalogModel.name.toString())
            catalogModelList.add(catalogModel)
        }

        Log.d("${catalogModelList.size}", catalogModelList.toString())
        return catalogModelList
    }
}

class HighObjectsFilterResponse(private val view: callback) : AsyncTask<String, String, ArrayList<HightFilterModelLevel>>() {

    override fun onPreExecute() {
        super.onPreExecute()
        //dialog.show()
    }

    override fun doInBackground(vararg params: String): ArrayList<HightFilterModelLevel>? {
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

            val finalJson = buffer.toString()
            Log.d("json", finalJson)

            return getHighFilterObjects(finalJson)

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

    override fun onPostExecute(result: ArrayList<HightFilterModelLevel>?) {
        super.onPostExecute(result)
        //dialog.dismiss()
        if (result != null) {
            view.onFiltersLoaded(result)
            Log.d("result", "onPostExecute")
        } else {
            //Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show()
        }
    }

    fun getHighFilterObjects(responseStr: String): ArrayList<HightFilterModelLevel>? {
        var hightFilterModelLevel: ArrayList<HightFilterModelLevel> = ArrayList()

        val response = JSONObject(responseStr)
        var data: JSONObject? = null

        try {
            data = JSONObject(response.getString("DATA"))
        } catch (e: JSONException) {
            //e.printStackTrace()
        }

        Log.d("data", data.toString())
        val iterator = data!!.keys()

        while (iterator.hasNext()) {
            val key = iterator.next() as String
            val finalObject = data.getJSONObject(key)
            val objectModel = HightFilterModelLevel()

            try {
                objectModel.id = finalObject.getString("ID")
                objectModel.name = finalObject.getString("NAME")
                objectModel.depth = finalObject.getString("DEPTH_LEVEL")
            } catch (e: JSONException) {
                //e.printStackTrace()
            }

            //CHILD
            var childs = ArrayList<SubFilterModelLevel>()

            try {

                val childsJsonArray = finalObject.getJSONObject("CHILD")

                val iteratorChilds = childsJsonArray.keys()

                Log.d("childs", childsJsonArray.toString())

                while (iteratorChilds.hasNext()) {
                    val childKey = iteratorChilds.next() as String
                    val childObject = childsJsonArray.getJSONObject(childKey)
                    val child = SubFilterModelLevel()

                    try {
                        objectModel.id = childObject.getString("ID")
                        objectModel.name = childObject.getString("NAME")
                        objectModel.depth = childObject.getString("DEPTH_LEVEL")
                    } catch (e: JSONException) {
                        //e.printStackTrace()
                    }

                    //CHILD
                    var filters = ArrayList<CatalogObjectsModel>()

                    try {

                        val filtersArray = finalObject.getJSONObject("CHILD")

                        val iteratorFilters = filtersArray.keys()

                        Log.d("filters", filtersArray.toString())

                        while (iteratorFilters.hasNext()) {
                            val filterdKey = iteratorFilters.next() as String
                            val filterObject = filtersArray.getJSONObject(filterdKey)
                            val filter = CatalogObjectsModel()

                            try {
                                objectModel.id = filterObject.getString("ID")
                                objectModel.name = filterObject.getString("NAME")
                            } catch (e: JSONException) {
                                //e.printStackTrace()
                            }

                            filters.add(filter)
                        }

                    }catch (e: JSONException){
                        //e.printStackTrace()
                    }

                    child.items = filters

                    childs.add(child)
                }

            } catch (e: JSONException) {
                //e.printStackTrace()
            }

            objectModel.items = childs

            hightFilterModelLevel.add(objectModel)
        }

        return hightFilterModelLevel
    }

    fun getCatalogObject(responseStr: String): List<CatalogObjectsModel> {

        val response = JSONObject(responseStr)

        var ITEMS: JSONObject? = null
        var DATA: JSONObject? = null

        val parentArray: JSONArray? = null

        try {
            DATA = JSONObject(response.getString("DATA"))
        } catch (e: JSONException) {
            //e.printStackTrace()
        }

        try {
            ITEMS = JSONObject(DATA!!.getString("ITEMS"))
            //parentArray = DATA.getJSONArray("ITEMS");
        } catch (e: JSONException) {
            //e.printStackTrace()
        }

        val catalogModelList = ArrayList<CatalogObjectsModel>()

        val gson = Gson()
        val array: JSONArray? = null

        val iterator = ITEMS!!.keys()
        while (iterator.hasNext()) {
            val key = iterator.next() as String
            val finalObject = ITEMS.getJSONObject(key)
            val catalogModel = CatalogObjectsModel()

            try {
                catalogModel.id = finalObject.getString("ID")
                catalogModel.name = finalObject.getString("NAME")
                catalogModel.property_address = finalObject.getString("PROPERTY_ADDRESS")

                val ImgObject: JSONObject? = finalObject.getJSONObject("DETAIL_PICTURE")

                if (ImgObject != null)
                    catalogModel.src = "http://ruinnet.idefa.ru/" + ImgObject.getString("SRC")

                catalogModel.preview_text = finalObject.getString("PREVIEW_TEXT")
                //catalogModel.audios = finalObject.getJSONArray("AUDIO_DATA").toString()

                //catalogModel.docs = finalObject.getJSONArray("DOCS_DATA")
                //catalogModel.publications = finalObject.getJSONArray("PUBLICATIONS_DATA")
                //catalogModel.videos = finalObject.getJSONArray("VIDEO_DATA")
                catalogModel.isFavorite = finalObject.getBoolean("IS_FAVORITE")
            } catch (e: JSONException) {

            }

            try {
                //PHOTO
                var photos = ArrayList<PhotoModel>()

                val photosJsonArray = finalObject.getJSONArray("PHOTOS_DATA")

                for (i: Int in 0..photosJsonArray.length() - 1) {
                    val photo = PhotoModel()
                    photo.name = photosJsonArray.getJSONObject(i).getString("NAME")
                    photo.src = "http://ruinnet.idefa.ru/" +
                            photosJsonArray.getJSONObject(i)
                                    .getJSONObject("DETAIL_PICTURE")
                                    .getString("SRC")
                    if (!photo.src.isNullOrEmpty()) {
                        photos.add(photo)
                    }
                }

                catalogModel.photos = photos

                //VIDEO
                var videos = ArrayList<VideoModel>()

                val videosJsonArray = finalObject.getJSONArray("VIDEO_DATA")

                for(i: Int in 0..videosJsonArray.length() - 1){
                    val video = VideoModel()
                    video.name = videosJsonArray.getJSONObject(i).getString("NAME")
                    video.youtubeCode = videosJsonArray.getJSONObject(i).getString("CODE")

                    if(!video.youtubeCode.isNullOrEmpty()){
                        videos.add(video)
                    }
                }

                catalogModel.videos = videos.toTypedArray()

                //AUDIO
                var audios = ArrayList<AudioModel>()

                val audiosJsonArray = finalObject.getJSONArray("AUDIO_DATA")

                for(i: Int in 0 until audiosJsonArray.length()){
                    val audio = AudioModel()
                    audio.name = audiosJsonArray .getJSONObject(i).getString("NAME")
                    audio.src = "http://ruinnet.idefa.ru/" +
                            audiosJsonArray .getJSONObject(i).getString("SRC")
                    if(!audio.src.isNullOrEmpty()){
                        audios.add(audio)
                    }
                }

                catalogModel.audios = audios.toTypedArray()

                //DOCS
                var docs = ArrayList<DocModel>()

                val docsJsonArray = finalObject.getJSONArray("AUDIO_DATA")

                for(i: Int in 0 until docsJsonArray.length()){
                    val doc = DocModel()
                    doc.name = docsJsonArray.getJSONObject(i).getString("NAME")
                    doc.code = "http://ruinnet.idefa.ru/" +
                            docsJsonArray.getJSONObject(i).getString("SRC")
                    if(!doc.code.isNullOrEmpty()){
                        docs.add(doc)
                    }
                }

                catalogModel.docs = docs.toTypedArray()

            } catch (e: JSONException){
                //e.printStackTrace()
            }

            catalogModelList.add(catalogModel)
        }

        return catalogModelList
    }
}
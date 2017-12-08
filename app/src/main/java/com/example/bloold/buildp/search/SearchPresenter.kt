package com.example.bloold.buildp.search

import android.content.Context
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import com.example.bloold.buildp.ListCatalogObjectsResponse
import com.example.bloold.buildp.R
import com.example.bloold.buildp.callback
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
class SearchPresenter(private val view: callback, private val context: Context) {
    private var searchType = "";
    fun findAddress(seq: String){
        searchType = "address"
        ListCatalogObjectsResponse(view).execute(getAddress(seq),searchType)
    }

    fun findObjects(seq: String){
        var url = ""

        val egrn = seq.toIntOrNull()
        if(egrn != null) {
            searchType ="egrn"
            url = getEGRN(seq)
        } else {
            searchType ="object"
            url = getObject(seq)
        }
        ListCatalogObjectsResponse(view).execute(url,searchType)
    }

    private fun getEGRN(egrn: String): String{
        return context.resources.getString(R.string.find_by_egrn, egrn)
    }

    private fun getAddress(address: String): String{
        return context.resources.getString(R.string.find_by_address, address)
    }

    private fun getObject(obj: String): String{
        return context.resources.getString(R.string.find_by_name, obj)
    }
}
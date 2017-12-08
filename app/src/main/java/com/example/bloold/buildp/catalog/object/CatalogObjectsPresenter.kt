package com.example.bloold.buildp.catalog.`object`

import com.example.bloold.buildp.ListCatalogObjectsResponse
import com.example.bloold.buildp.callback

/**
 * Created by bloold on 18.10.17.
 */

class CatalogObjectsPresenter(private val view: callback) {

    fun getCatalogObjects(url: String) {
        ListCatalogObjectsResponse(view).execute(url)
    }
}
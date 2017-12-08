/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bloold.buildp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
@JsonIgnoreProperties(ignoreUnknown = true)
class MyItem : ClusterItem {
    @get: JsonProperty("ADDRESS")
    var title: String? = null
    @get: JsonProperty("NAME")
    var snippet: String? = null
    @get: JsonProperty("OBJECT_ID")
    var id: Int=0
    /*@get: JsonProperty("ID")
    var id: Int =0*/
    @get: JsonProperty("LAT")
    var lat:Double=0.0
    @get: JsonProperty("LNG")
    var lng:Double=0.0
    @get: JsonProperty("IS_FAVORITE")
    var isFavourite:Boolean?=null

    override fun getPosition(): LatLng = LatLng(lat,lng)

    override fun toString(): String {
        return snippet?:""
    }
}

package com.example.bloold.buildp.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by sagus on 27.11.2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class PropertyFile {
    @get: JsonProperty("SRC")
    var src: String? = null
}
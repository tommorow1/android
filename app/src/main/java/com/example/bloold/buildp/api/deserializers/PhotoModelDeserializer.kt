package com.example.bloold.buildp.api.deserializers

import com.example.bloold.buildp.model.PhotoModel
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 19.06.17.
 */

class PhotoModelDeserializer : JsonDeserializer<PhotoModel>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, cntx: DeserializationContext): PhotoModel {
        val rawData = jp.codec.readTree<JsonNode>(jp)
        val item=PhotoModel()
        item.name=rawData.get("NAME").asText()
        rawData.get("DETAIL_PICTURE")?.let {
            item.src=it.get("SRC").asText()
            item.id=it.get("ID").asLong()

        }
        return item
    }
}
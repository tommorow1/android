package com.example.bloold.buildp.api.deserializers

import com.example.bloold.buildp.model.AudioModel
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 19.06.17.
 */

class AudioDataDeserializer : JsonDeserializer<AudioModel>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, cntx: DeserializationContext): AudioModel {
        val rawData = jp.codec.readTree<JsonNode>(jp)
        val item=AudioModel()
        item.name=rawData.get("NAME").asText()
        rawData.get("PROPERTY_FILE")?.let {
            item.id=it.get("ID").asLong()
            item.src=it.get("SRC").asText()
        }
        return item
    }
}
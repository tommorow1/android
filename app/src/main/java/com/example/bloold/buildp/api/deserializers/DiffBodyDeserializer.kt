package com.example.bloold.buildp.api.deserializers

import com.example.bloold.buildp.model.Suggestion
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import java.io.IOException

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 19.06.17.
 */

class DiffBodyDeserializer : JsonDeserializer<Suggestion.SuggestionDiff.DiffBody>() {
    @Throws(IOException::class)
    override fun deserialize(jp: JsonParser, cntx: DeserializationContext): Suggestion.SuggestionDiff.DiffBody {
        val rawData = jp.codec.readTree<JsonNode>(jp)
        val item=Suggestion.SuggestionDiff.DiffBody()

        val wasNode=rawData.get("IT_WAS")
        if(wasNode.isArray)
            wasNode.forEach {
                if(!item.wasValue.isNullOrEmpty())
                    item.wasValue+=", "+it.textValue()
                else item.wasValue=it.textValue()
            }
        else item.wasValue=wasNode.textValue()

        val becomeNode=rawData.get("HAS_BECOME")
        if(becomeNode.isArray)
            becomeNode.forEach {
                if(!item.newValue.isNullOrEmpty())
                    item.newValue+=", "+it.textValue()
                else item.newValue=it.textValue()
            }
        else item.newValue=becomeNode.textValue()
        return item
    }
}
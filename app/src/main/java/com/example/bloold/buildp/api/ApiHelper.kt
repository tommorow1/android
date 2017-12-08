package com.example.bloold.buildp.api

import com.example.bloold.buildp.model.VideoModel


/**
 * Created by sagus on 18.11.2017.
 */
class ApiHelper {
    companion object {
        val SUGGESTION_ALL=""
        val SUGGESTION_ON_MODERATION="check/"
        val SUGGESTION_APPROVED="approved/"
        val SUGGESTION_DECLINED="declined/"
        val suggestionParams: Array<String> by lazy { arrayOf("OBJECT_ID", "OBJECT_NAME", "OBJECT_PICTURE", "STATE_NAME", "DATE_CREATE") }
        val notificationParams: Array<String> by lazy { arrayOf("ID", "NOTICE_DATA","NOTIFY_READ","DATE_CREATE") }
        val questsParams: Array<String> by lazy { arrayOf("ID",
                "NAME",
                "DETAIL_PICTURE",
                "PREVIEW_TEXT",
                "PROPERTY_STATUS",
                "PARTICIPATE",
                "PROPERTY_POINTS",
                "PROPERTY_PARTICIPANTS",
                "PROPERTY_TYPE")}

        val questsFullParams: Array<String> by lazy { questsParams+ arrayOf(
                "DETAIL_TEXT",
                "PARTICIPANTS_DATA",
                "OBJECTS_DATA")}

        val defaultParams: Array<String> by lazy { arrayOf("ID", "NAME", "DETAIL_PICTURE", "IS_FAVORITE","PROPERTY_MAP") }
        val mapParams: Array<String> by lazy { arrayOf("OBJECT_ID", "NAME", "ADDRESS", "MAP_TYPE","PROPERTY_MAP", "LAT", "LNG") }
        val fullParams: Array<String> by lazy {
            defaultParams + arrayOf(
                    "PREVIEW_TEXT",
                    "PROPERTY_ADDRESS",
                    "PHOTOS_DATA",
                    "DOCS_DATA",
                    "PUBLICATIONS_DATA",
                    "VIDEO_DATA",
                    "AUDIO_DATA",
                    "DETAIL_PAGE_URL",
                    "PROPERTY_MAP",
                    "PROPERTY_UNESCO",
                    "PROPERTY_TYPE",
                    "PROPERTY_VALUE_CATEGORY",
                    "PROPERTY_CONDITION",//Описание состояния
                    "CONDITION_ID",//Оценка состояния
                    "PROPERTY_LAT",
                    "PROPERTY_LNG"
            )
        }

        fun generateSearchParams(type: String?, query: String?):HashMap<String,String>? {
            val res=HashMap<String,String>()
            if(type!=null&&query!=null)
                res.apply { put("filter[0][?$type]", query) }
            return res
        }
        fun generateUploadFileParams(name:String, base64FileData:String, key: Long)
                = HashMap<String,String>()
                    .apply { put("FILES[$key][DATA]", base64FileData) }
                    .apply { put("FILES[$key][NAME]", name) }
        fun generateChangedPhotosParams(photoFiles:List<Long>):HashMap<String,String>
                =HashMap<String,String>().apply { photoFiles.forEach {
                put("FORM[PROPS][PHOTOS][$it][DETAIL_PICTURE]", it.toString())
                put("FORM[PROPS][PHOTOS][$it][CHANGED]", "1") }}
        fun generateChangedAudiosParams(photoFiles:List<Long>):HashMap<String,String>
                =HashMap<String,String>().apply { photoFiles.forEach {
            put("FORM[PROPS][AUDIO][$it][PROPS][FILE]", it.toString())
            put("FORM[PROPS][AUDIO][$it][CHANGED]", "1") }}
        fun generateArchiveDocsParams(archiveDocsFiles:List<Long>):HashMap<String,String>
                =HashMap<String,String>().apply { archiveDocsFiles.forEach {
            put("FORM[PROPS][DOCS][$it][PROPS][FILE][VALUE]", it.toString())
            put("FORM[PROPS][DOCS][$it][CHANGED]", "1") }}
        fun generatePublicationParams(archiveDocsFiles:List<Long>):HashMap<String,String>
                =HashMap<String,String>().apply { archiveDocsFiles.forEach {
            put("FORM[PROPS][PUBLICATIONS][$it][PROPS][FILE][VALUE]", it.toString())
            put("FORM[PROPS][PUBLICATIONS][$it][CHANGED]", "1") }}
        fun generateChangedVideoParams(videoForUploading:List<VideoModel>):HashMap<String,String>
        {
            val data = HashMap<String,String>()
            for (i in 0 until videoForUploading.size)
            {
                videoForUploading[i].youtubeCode?.let {
                    data.put("FORM[PROPS][VIDEO][$i][CODE]", it)
                    data.put("FORM[PROPS][VIDEO][$i][CHANGED]", "1")
                }
            }
            return data
        }
    }
}
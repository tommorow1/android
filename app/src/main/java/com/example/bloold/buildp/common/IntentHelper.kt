package com.example.bloold.buildp.common

/**
 * Created by sagus on 18.11.2017.
 */
class IntentHelper {
    companion object
    {
        val ACTION_TOGGLE_FAVOURITE="toggleFavourite"
        val ACTION_SEND_PUSH_TOKEN="sendPushToken"
        val ACTION_SEND_NOTIFICATION_READ="notificationRead"
        val ACTION_NEED_REAUTH="needReauth"

        val EXTRA_SORT_OBJECT="sortObject"
        val EXTRA_PUSH_TOKEN="pushToken"

        val EXTRA_QUERY_TYPE="queryType"
        val EXTRA_QUERY_STRING="queryString"
        val EXTRA_SUGGESTION="suggestion"

        val EXTRA_OBJECT_ID="ObjId"
        val EXTRA_OBJECT_LIST="objectList"
        val EXTRA_PARTICIPANT_LIST="participantList"
        val EXTRA_QUEST_ID="questId"
        val EXTRA_CATEGORY_ID="categoryId"
        val EXTRA_PHOTO_DATA_ARRAY="photoDataArr"
        val EXTRA_IMAGE_URL="imageUrl"
        val EXTRA_SEARCH_TEXT="searchText"
        val EXTRA_SEARCH_TYPE="searchType"
        val EXTRA_ORIGIN="Origin"
        val EXTRA_DESTINATION="Destination"
        val EXTRA_AUDIO_MODEL_LIST="audioModelList"
        val EXTRA_DOC_PUB_MODEL_LIST="docPubModelList"
        val EXTRA_VIDEO_MODEL_LIST="videoModelList"

        val EXTRA_SUGGESTION_TYPE="suggestionType"
        val EXTRA_QUEST_STATUS="questStatus"

        val EXTRA_LATITUDE="latitude"
        val EXTRA_LONGITUDE="longitude"
        val EXTRA_ADDRESS="address"
        val EXTRA_OBJECT_NAME="objName"

        val EXTRA_ERROR_MSG="errorMsg"
        val EXTRA_VALUE="value"

        val EXTRA_TITLE="title"
        val EXTRA_URL="urlToOpen"
    }
}
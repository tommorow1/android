package com.example.bloold.buildp.api

import com.example.bloold.buildp.api.data.*
import com.example.bloold.buildp.model.*
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Leonov Oleg, http://pandorika-it.com on 24.05.16.
 */
interface ServerApi {
    @POST("user/set-push-token/")
    @FormUrlEncoded
    fun setPushToken(@Field("PUSH_TOKEN") pushToken:String): Single<Response<BaseResponseWithoutData>>

    @GET("directory/type/")
    fun getObjectTypes(): Single<BaseResponseWithDataObject<ObjectType>>

    @GET("directory/value-category/")
    fun getValueCategories(): Single<BaseResponseWithDataObject<ObjectCategory>>

    @GET("directory/protective-status/")
    fun getProtectiveStatuses(): Single<BaseResponseWithDataObject<ProtectiveStatus>>

    @GET("directory/type-catalog-structure/")
    fun getCategories(): Single<BaseResponse<Category>>
    @GET("object/list/")
    fun getCatalogObjects(@QueryMap filter:Map<String, String>?,
                          @Query("limit") limit: Int, @Query("page") page: Int,
                          @Query("filter[IBLOCK_SECTION_ID][0]") categoryId: String?=null,
                          @QueryMap searchQuery:Map<String, String>?=HashMap(),
                          @Query("filter[INCLUDE_SUBSECTIONS]") includeSubsections: String="Y",
                          @Query("select[]") selectParams: Array<String> = ApiHelper.defaultParams): Single<BaseResponseWithDataObject<CatalogObject>>
    /*** Избранное */
    @GET("object/favorite/list/")
    fun getFavourite(): Single<BaseResponseWithDataObject<FavouriteObject>>

    @GET("directory/type-map-structure/")
    fun getMapStructure(): Single<BaseResponse<CatalogObject>>
    @POST("object/edit/")
    @FormUrlEncoded
    fun editObject(@Field("FORM[ID]") objectId:Int,
                   @Field("FORM[PROPS][CONDITION][DETAIL_TEXT]") conditionDescription:String?,
                   @Field("FORM[PROPS][CONDITION][PROPS][MARK]") conditionMarkId:Int?,
                   @FieldMap extraFields:Map<String,String>): Single<Response<BaseResponseWithoutData>>
    //------ Состояния объектов ---------
    @GET("directory/condition-mark/")
    fun getConditionMark(): Single<BaseResponse<ConditionMark>>
    /*** Текущее состояние объекта */
    @GET("object/index/")
    fun getObjectConditionMark(@Query("OBJECT_ID") objectId: Int,
                               @Query("limit") limit: Int=1,
                               @Query("page") page: Int=1,
                               @Query("select[]") condition: String="CONDITION"): Single<CurrentUserCondition>
    @POST("object/condition/send/")
    fun sendCondition(@Query("OBJECT_ID") objectId:Int,
                      @Query("MESSAGE") message:String,
                      @Query("MARK_ID") int:Int,
                      @Query("DATE") date_dd_MM_YY:String,
                      @Query("PHOTO_ID") photoId:Int?=null): Single<Response<Void>>
    //-------
    @GET("object/index/")
    fun searchMapObjects(@QueryMap filter:Map<String, String>?,
                         @Query("limit") limit: Int?=null, @Query("page") page: Int?=null,
                         @Query("select[]") selectParams: Array<String> = ApiHelper.mapParams): Single<BaseResponseWithDataObject<MyItem>>
    @GET("object/index/")
    fun searchMapObjectsByName(@Query("filter[0][?NAME]=") query: String,
                               @Query("select[]") selectParams: Array<String> = ApiHelper.mapParams): Single<BaseResponseWithDataObject<MyItem>>
    @POST("object/favorite/toggle/")
    @FormUrlEncoded
    fun toggleFavourite(@Field("OBJECT_ID") objectId:Int): Single<BaseResponseWithDataObject<Void>>
    @POST("file/upload/")
    @FormUrlEncoded
    fun uploadFile(@FieldMap uploadFileData:Map<String,String>): Single<Response<BaseResponseWithDataObject<Long>>>

    /*** Добавление объекта */
    @POST("object/add/")
    @FormUrlEncoded
    fun addObject(@Field("FORM[NAME]") name:String,
                   @Field("FORM[PROPS][PUBLIC_NAME][0]") folkName:String,
                   @Field("FORM[PROPS][ADDRESS]") address:String,
                   @Field("FORM[PROPS][MAP]") latLng:String,
                   @Field("FORM[DETAIL_TEXT]") description:String?,
                   @Field("FORM[PROPS][ARCHITECT]") architect:String?,
                   @Field("FORM[PROPS][DATE_CREATE][VALUE]") createDate_dd_mm_yy:String?,
                   @Field("FORM[PROPS][DATE_CREATE][DESCRIPTION]") createDateDescription:String?,
                   @Field("FORM[PROPS][DATE_RECONSTRUCTION][0][VALUE]") reconstructionDate_dd_mm_yy:String?,
                   @Field("FORM[PROPS][TYPE]") objectType:String?,
                   @Field("FORM[PROPS][VALUE_CATEGORY]") valueCategory:String?,
                   @Field("FORM[PROPS][PROTECTIVE_STATUS]") protectiveStatus:String?,
                   @Field("FORM[PROPS][UNESCO]") isUnesco:String?,
                   @Field("FORM[PROPS][VALUABLE]") isValuable:String?,
                   @Field("FORM[PROPS][HISTORIC_SETTLEMENT]") isHistoricSettlement:String?): Single<Response<BaseResponseWithoutData>>

    /*** Редактирование объекта */
    @POST("object/edit/")
    @FormUrlEncoded
    fun editObject(@Field("FORM[ID]") objectId:Int,
                   @Field("FORM[NAME]") name:String,
                  @Field("FORM[PROPS][PUBLIC_NAME][0]") folkName:String,
                  @Field("FORM[PROPS][ADDRESS]") address:String,
                  @Field("FORM[PROPS][MAP]") latLng:String,
                  @Field("FORM[DETAIL_TEXT]") description:String?,
                  @Field("FORM[PROPS][ARCHITECT]") architect:String?,
                  @Field("FORM[PROPS][DATE_CREATE][VALUE]") createDate_dd_mm_yy:String?,
                  @Field("FORM[PROPS][DATE_CREATE][DESCRIPTION]") createDateDescription:String?,
                  @Field("FORM[PROPS][DATE_RECONSTRUCTION][0][VALUE]") reconstructionDate_dd_mm_yy:String?,
                  @Field("FORM[PROPS][TYPE]") objectType:String?,
                  @Field("FORM[PROPS][VALUE_CATEGORY]") valueCategory:String?,
                  @Field("FORM[PROPS][PROTECTIVE_STATUS]") protectiveStatus:String?,
                  @Field("FORM[PROPS][UNESCO]") isUnesco:String?,
                  @Field("FORM[PROPS][VALUABLE]") isValuable:String?,
                  @Field("FORM[PROPS][HISTORIC_SETTLEMENT]") isHistoricSettlement:String?): Single<Response<BaseResponseWithoutData>>

    @POST("object/edit/")
    @FormUrlEncoded
    fun addArchiveMaterials(@Field("FORM[ID]") objectId:Int,
                   @Field("FORM[PROPS][DOCS][0][NAME]") docName:String,
                   @Field("FORM[PROPS][DOCS][0][PROPS][ARCHIVE_NAME]") archiveName:String,
                   @Field("FORM[PROPS][DOCS][0][PROPS][DOCUMENT_CODE]") docCode:Int?,
                   @Field("FORM[PROPS][DOCS][0][PROPS][FILE][DESCRIPTION]") description:String?,
                   @FieldMap materials:Map<String,String>): Single<Response<Void>>
    @POST("object/edit/")
    @FormUrlEncoded
    fun addPublication(@Field("FORM[ID]") objectId:Int,
                            @Field("FORM[PROPS][PUBLICATIONS][0][NAME]") pubName:String,
                            @Field("FORM[PROPS][PUBLICATIONS][0][DETAIL_PICTURE] ") coverPictureId:Long?,
                            @FieldMap materials:Map<String,String>): Single<Response<Void>>

    /*** Предложения */
    @GET("object/suggestions/{type}")
    fun getSuggestions(@Path("type", encoded = true) suggestionType:String,
                       @Query("limit") limit: Int, @Query("page") page: Int,
                       @Query("GET_DIFF") yOrN:String="Y",
                       @Query("select[]") selectParams: Array<String> = ApiHelper.suggestionParams): Single<Response<BaseResponseWithDataObject<Suggestion>>>
    /*** Лента уведомлений */
    @GET("notices/list/")
    fun getNotifications(@Query("limit") limit: Int, @Query("page") page: Int,
                         @Query("select[]") selectParams: Array<String> = ApiHelper.notificationParams): Single<Response<BaseResponseWithDataObject<NotificationInfo>>>
    /*** Список квестов */
    @GET("object/quest/list/")
    fun getQuests(@Query("limit") limit: Int, @Query("page") page: Int,
                         @Query("select[]") selectParams: Array<String> = ApiHelper.questsParams): Single<Response<BaseResponseWithDataObject<Quest>>>

    /*** Список типов для моих квестов */
    @GET("directory/quest-filter/")
    fun getQuestTypes(): Single<Response<QuestTypes>>
    /*** Список моих квестов */
    @GET("user/quest/list/")
    fun getMyQuests(@Query("filter[TYPE]") questType:String,
                    @Query("limit") limit: Int, @Query("page") page: Int,
                  @Query("select[]") selectParams: Array<String> = ApiHelper.questsParams): Single<Response<BaseResponseWithDataObject<Quest>>>

    @GET("object/quest/list/")
    fun getQuestDetails(@Query("filter[ID][0]") questId: Int,
                        @Query("select[]") selectParams: Array<String> = ApiHelper.questsFullParams): Single<BaseResponseWithDataObject<Quest>>
    @POST("object/quest/toggle/")
    @FormUrlEncoded
    fun toggleQuestParticipate(@Field("QUEST_ID") questId:Int): Single<Response<BaseResponseWithoutData>>

    /*** Отметить все уведомления прочитанными */
    @POST("notices/set-read/")
    fun setNotificationsRead(): Single<Response<BaseResponseWithoutData>>

    /*** Отправить обратную связь */
    @POST("feedback/")
    @FormUrlEncoded
    fun sendFeedback(@Field("NAME") name:String,
                     @Field("EMAIL") email:String?,
                     @Field("PHONE") phone:String?,
                     @Field("MSG") message:String,
                     @Field("ATTACH") fileId:Long?=null): Single<Response<FeedbackResponse>>

    /*** Настройки уведомлений */
    @GET("user/notice/types/")
    fun getNotificationSettings(): Single<Response<NotificationSettings>>

    @POST("user/notice/types/")
    @FormUrlEncoded
    fun setNotificationSettings(@Field("NOTICES[]") notices:ArrayList<String>): Single<Response<BaseResponseWithoutData>>
}

package com.example.bloold.buildp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.text.format.DateFormat
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ApiHelper
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.api.data.BaseResponseWithoutData
import com.example.bloold.buildp.api.data.CatalogObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.NetworkActivity
import com.example.bloold.buildp.components.SpinnerWithoutLPaddingAdapter
import com.example.bloold.buildp.components.UIHelper
import com.example.bloold.buildp.databinding.ActivityAddObjectBinding
import com.example.bloold.buildp.model.ObjectCategory
import com.example.bloold.buildp.model.ObjectType
import com.example.bloold.buildp.model.ProtectiveStatus
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*


class AddObjectActivity : NetworkActivity() {
    private lateinit var mBinding: ActivityAddObjectBinding
    private var latitude:Double?=null
    private var longitude:Double?=null
    private var createDate: Date?=null
    private var rebuildDate: Date?=null
    private var catalogObject: CatalogObject?=null

    companion object {
        private val EXTRA_FULL_FORM="fullForm"
        fun launch(act: Activity, fullForm:Boolean = false, objectId:Int?=null)
        {
            act.startActivity(Intent(act, AddObjectActivity::class.java)
                    .putExtra(EXTRA_FULL_FORM, fullForm)
                    .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_object)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.tvAddObjectRules.movementMethod = LinkMovementMethod.getInstance()

        if(intent.getBooleanExtra(EXTRA_FULL_FORM, false))
            showFullForm()
        (intent.getSerializableExtra(IntentHelper.EXTRA_OBJECT_ID) as Int?)?.let {
            loadObjectDetails(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(mBinding.llFullBlock.visibility==View.VISIBLE)
            hideFullForm()
        else super.onBackPressed()
    }

    private fun loadObjectDetails(objectId:Int)
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getCatalogObjects(HashMap<String,String>().apply { put("filter[ID][0]", objectId.toString()) },
                1, 1,  selectParams = ApiHelper.fullParams)
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .doFinally {
                    if(catalogObject!=null)
                    {
                        updateUI()
                        showProgress(false)
                    }
                    else finish()
                }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<CatalogObject>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<CatalogObject>) {
                        catalogObject=result.data?.items?.firstOrNull()
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))
    }
    private fun updateUI()
    {
        catalogObject?.let {
            mBinding.tvTitle.setText(R.string.edit_object_title)
            mBinding.etName.setText(it.name)
            mBinding.tvAddress.text=it.propertyAddress
            latitude=it.latitude
            longitude=it.longitude
            mBinding.etObjectDescription.setText(it.previewText)
            mBinding.scUnesco.isChecked=it.isUnesco=="Y"
            mBinding.scImportantObject.isChecked=it.isValuable=="Y"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK)
        {
            if(requestCode==ChooseLocationActivity.REQUEST_CHOOSE_LOCATION)
            {
                data?.let {
                    mBinding.tvAddress.text=data.getStringExtra(IntentHelper.EXTRA_ADDRESS)
                    latitude=data.getSerializableExtra(IntentHelper.EXTRA_LATITUDE) as Double?
                    longitude=data.getSerializableExtra(IntentHelper.EXTRA_LONGITUDE) as Double?
                }
            }
        }
    }
    private fun showFullForm()
    {
        if(mBinding.spObjectType.adapter==null) {
            //Загружаем данные для спиннеров
            loadObjectTypes()
            loadObjectValueCategoriesTypes()
            loadProtectiveStatuses()
        }
        mBinding.tvFullForm.visibility=View.GONE
        mBinding.llFullBlock.visibility=View.VISIBLE
    }
    private fun hideFullForm()
    {
        mBinding.tvFullForm.visibility=View.VISIBLE
        mBinding.llFullBlock.visibility=View.GONE
    }
    private fun loadObjectTypes()
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getObjectTypes()
                .compose(RxHelper.applySchedulers())
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<ObjectType>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<ObjectType>) {
                        result.data?.items?.let {
                            val objectType= ObjectType()
                            objectType.value=getString(R.string.choose)
                            mBinding.spObjectType.adapter = SpinnerWithoutLPaddingAdapter(this@AddObjectActivity,
                                    android.R.layout.simple_spinner_dropdown_item, arrayOf(objectType) + it)
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }))
    }
    private fun loadObjectValueCategoriesTypes()
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getValueCategories()
                .compose(RxHelper.applySchedulers())
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<ObjectCategory>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<ObjectCategory>) {
                        result.data?.items?.let {
                            val objectType= ObjectCategory()
                            objectType.value=getString(R.string.choose)
                            mBinding.spObjectCategory.adapter = SpinnerWithoutLPaddingAdapter(this@AddObjectActivity,
                                    android.R.layout.simple_spinner_dropdown_item, arrayOf(objectType) + it)
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }))
    }
    private fun loadProtectiveStatuses()
    {
        compositeDisposable.add(ServiceGenerator.serverApi.getProtectiveStatuses()
                .compose(RxHelper.applySchedulers())
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<ProtectiveStatus>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<ProtectiveStatus>) {
                        result.data?.items?.let {
                            val objectType= ProtectiveStatus()
                            objectType.value=getString(R.string.choose)
                            mBinding.spProtectionStatus.adapter = SpinnerWithoutLPaddingAdapter(this@AddObjectActivity,
                                    android.R.layout.simple_spinner_dropdown_item, arrayOf(objectType) + it)
                        }
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }))
    }
    fun onBuildDateClick(v:View)
    {
        UIHelper.showDatePickerDialog(this, Calendar.getInstance().apply { createDate?.let { time=it } })
                .onDateSetListener= DatePickerDialog.OnDateSetListener({ _,year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            createDate = calendar.time
            updateCreateDate()
        })
    }
    fun onReBuildDateClick(v:View)
    {
        UIHelper.showDatePickerDialog(this, Calendar.getInstance()
                .apply { rebuildDate?.let { time=it } })
                .onDateSetListener= DatePickerDialog.OnDateSetListener({ _,year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            rebuildDate = calendar.time
            updateReBuildDate()
        })
    }
    private fun updateCreateDate()
    {
        if (createDate != null)
            mBinding.tvBuildDate.text = DateFormat.getMediumDateFormat(this).format(createDate)
        else
            mBinding.tvBuildDate.text = ""
    }
    private fun updateReBuildDate()
    {
        if (rebuildDate != null)
            mBinding.tvReBuildDate.text = DateFormat.getMediumDateFormat(this).format(rebuildDate)
        else
            mBinding.tvReBuildDate.text = ""
    }
    fun onGoToFullFormClick(v:View)
    {
        showFullForm()
    }
    fun onAddObjectClick(v:View)
    {
        var error = UIHelper.setErrorIfEmpty(mBinding.etName, getString(R.string.need_fill_this_field))
        if(latitude==null||longitude==null)
        {
            error=true
            Toast.makeText(this, R.string.need_address_for_adding_object, Toast.LENGTH_LONG).show()
        }
        if(error) return

        val objectType=mBinding.spObjectType.selectedItem as ObjectType?
        val objectCategory=mBinding.spObjectCategory.selectedItem as ObjectCategory?
        val protectiveStatus=mBinding.spProtectionStatus.selectedItem as ProtectiveStatus?
        if(catalogObject==null) {
            //Новый объект
            compositeDisposable.add(ServiceGenerator.serverApi.addObject(mBinding.etName.text.toString(),
                    mBinding.etFolkName.text.toString(),
                    mBinding.tvAddress.text.toString(),
                    latitude.toString() + "," + longitude.toString(),
                    mBinding.etObjectDescription.text.toString(),
                    mBinding.etArchitect.text.toString(),
                    if (createDate != null) SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(createDate) else null,
                    mBinding.etDateComment.text.toString(),
                    if (rebuildDate != null) SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(rebuildDate) else null,
                    if (objectType?.id.isNullOrEmpty()) null else objectType?.value,
                    if (objectCategory?.id.isNullOrEmpty()) null else objectCategory?.value,
                    if (protectiveStatus?.id.isNullOrEmpty()) null else protectiveStatus?.value,
                    getSwitchState(mBinding.scUnesco),
                    getSwitchState(mBinding.scImportantObject),
                    getSwitchState(mBinding.scHistoryColonyObject))
                    .compose(RxHelper.applySchedulers())
                    .doOnSubscribe { showProgress(true) }
                    .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                        override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                            if (result.isSuccessful && result.body()?.code == 200&&result.body()?.code==200) {
                                Toast.makeText(this@AddObjectActivity, R.string.object_added, Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                UIHelper.showServerError(result, this@AddObjectActivity)
                                showProgress(false)
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            showProgress(false)
                            if (e is UnknownHostException || e is ConnectException)
                                Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        }
                    }))
        }
        else
        {
            //Редактирование объекта
            compositeDisposable.add(ServiceGenerator.serverApi.editObject(catalogObject?.id?:-1, mBinding.etName.text.toString(),
                    mBinding.etFolkName.text.toString(),
                    mBinding.tvAddress.text.toString(),
                    latitude.toString() + "," + longitude.toString(),
                    mBinding.etObjectDescription.text.toString(),
                    mBinding.etArchitect.text.toString(),
                    if (createDate != null) SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(createDate) else null,
                    mBinding.etDateComment.text.toString(),
                    if (rebuildDate != null) SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(rebuildDate) else null,
                    if (objectType?.id.isNullOrEmpty()) null else objectType?.value,
                    if (objectCategory?.id.isNullOrEmpty()) null else objectCategory?.value,
                    if (protectiveStatus?.id.isNullOrEmpty()) null else protectiveStatus?.value,
                    getSwitchState(mBinding.scUnesco),
                    getSwitchState(mBinding.scImportantObject),
                    getSwitchState(mBinding.scHistoryColonyObject))
                    .compose(RxHelper.applySchedulers())
                    .doOnSubscribe { showProgress(true) }
                    .subscribeWith(object : DisposableSingleObserver<Response<BaseResponseWithoutData>>() {
                        override fun onSuccess(result: Response<BaseResponseWithoutData>) {
                            if (result.isSuccessful && result.body()?.code == 200) {
                                Toast.makeText(this@AddObjectActivity, R.string.object_edited, Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                UIHelper.showServerError(result, this@AddObjectActivity)
                                showProgress(false)
                            }
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                            showProgress(false)
                            if (e is UnknownHostException || e is ConnectException)
                                Toast.makeText(applicationContext, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(applicationContext, R.string.server_error, Toast.LENGTH_SHORT).show()
                        }
                    }))
        }
    }
    private fun getSwitchState(switch:SwitchCompat):String?
    {
        return if(mBinding.llFullBlock.visibility==View.VISIBLE)
            if(switch.isChecked) "Y" else "N"
        else null
    }
    fun onChooseAddressClick(v:View)
    {
        ChooseLocationActivity.launch(this, ChooseLocationActivity.REQUEST_CHOOSE_LOCATION, latitude, longitude)
    }

    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
}

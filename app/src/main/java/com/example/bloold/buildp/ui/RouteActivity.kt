package com.example.bloold.buildp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.bloold.buildp.R
import com.example.bloold.buildp.api.ServiceGenerator
import com.example.bloold.buildp.api.data.BaseResponseWithDataObject
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.common.RxHelper
import com.example.bloold.buildp.components.NetworkActivity
import com.example.bloold.buildp.databinding.ActivityRouteBinding
import com.example.bloold.buildp.model.MyItem
import io.reactivex.observers.DisposableSingleObserver
import java.net.*

class RouteActivity : NetworkActivity()
{
    private lateinit var mBinding:ActivityRouteBinding

    //----- Получение координа пользователя ------
    private val PERMISSIONS_LOCATION = 234
    private var userLocation: Location? = null
    private var locationListener: LocationListener? = null
    //Получили актуальные координаты пользователя(не последние)
    private var gotActualLocation = false

    private var origin:MyItem?=null
    private var destination:MyItem?=null

    private var itemObject:MyItem?=null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_route)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding.atvOrigin.setAdapter<RouteActivity.GooglePlacesAutocompleteAdapter>(
                RouteActivity.GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_dropdown_item_1line))
        mBinding.atvOrigin.setOnItemClickListener { parent, v, position, id ->
            val selection = parent.getItemAtPosition(position) as MyItem
            mBinding.atvOrigin.setText(selection.snippet)
            origin=selection
        }

        mBinding.atvDestination.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            val selection = parent.getItemAtPosition(position) as MyItem
            mBinding.atvDestination.setText(selection.snippet)
            destination=selection
        }

        loadObject(intent.getIntExtra(IntentHelper.EXTRA_OBJECT_ID, -1))
    }

    override fun onResume() {
        super.onResume()
        checkGPSPermissions()
    }
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }
    private fun checkGPSPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_LOCATION)
        } else
            startGettingUserLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGettingUserLocation()
                } else {
                    AlertDialog.Builder(this)
                            .setMessage(R.string.need_gps_permission)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok) { _, _ -> checkGPSPermissions() }
                            .setNegativeButton(android.R.string.cancel) { _, _ -> this.finish() }.show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }
    private fun startGettingUserLocation() {
        //Пока определяются текущие координаты, мы получаем посление
        if (userLocation == null) {
            userLocation = fetchLastLocation()
        }
        if (!gotActualLocation) fetchActualUserLocation()//Запускаем получение актуальных координат
    }
    // -------- Получение местоположения пользователя ----------
    private fun fetchLastLocation(): Location? {
        try {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return lastLocation
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            Toast.makeText(this, R.string.gps_not_enabled, Toast.LENGTH_LONG).show()
        }

        return null
    }
    private fun fetchActualUserLocation() {
        // Define a listener that responds to location updates
        if (locationListener == null) {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Called when a new location is found by the network location provider.
                    userLocation = location
                    stopLocationUpdates()
                    gotActualLocation = true
                    locationListener = null
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            }
        }

        try {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            Toast.makeText(this, R.string.gps_not_enabled, Toast.LENGTH_LONG).show()
        }
    }
    private fun stopLocationUpdates() {
        if (locationListener != null) {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(locationListener)
        }
    }

    fun onRouteClick(v:View)
    {
        if (userLocation == null&&origin==null) {
            Toast.makeText(this@RouteActivity, R.string.need_set_origin_location, Toast.LENGTH_SHORT).show()
            return
        }
        if (itemObject == null&&destination==null) {
            Toast.makeText(this@RouteActivity, R.string.need_set_destination_location, Toast.LENGTH_SHORT).show()
            return
        }
        RouteMapActivity.launch(this, origin?.lat?:userLocation?.latitude?:0.0, origin?.lng?:userLocation?.longitude?:0.0,
                destination?.lat?:itemObject?.lat?:0.0, destination?.lng?:itemObject?.lng?:0.0)
    }
    private fun showProgress(showProgress: Boolean) {
        mBinding.flLoading.visibility = if (showProgress) View.VISIBLE else View.GONE
    }
    private fun loadObject(objectId:Int)
    {
        compositeDisposable.add(ServiceGenerator.serverApi.searchMapObjects(HashMap<String,String>().apply { put("filter[OBJECT_ID]",objectId.toString()) })
                .compose(RxHelper.applySchedulers())
                .doOnSubscribe { showProgress(true) }
                .doFinally {
                    showProgress(false)
                    if(itemObject==null)
                    {
                        Toast.makeText(applicationContext, R.string.object_not_found, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else updateUI()
                }
                .subscribeWith(object : DisposableSingleObserver<BaseResponseWithDataObject<MyItem>>() {
                    override fun onSuccess(result: BaseResponseWithDataObject<MyItem>) {
                        itemObject=result.data?.items?.firstOrNull()
                    }
                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is UnknownHostException || e is ConnectException)
                            Toast.makeText(this@RouteActivity, R.string.error_check_internet, Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(this@RouteActivity, R.string.server_error, Toast.LENGTH_SHORT).show()
                    }
                }))
    }
    private fun updateUI()
    {
        itemObject?.let {
            mBinding.atvDestination.setText(it.snippet)
            mBinding.atvDestination.setAdapter<RouteActivity.GooglePlacesAutocompleteAdapter>(
                    RouteActivity.GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_dropdown_item_1line))
        }
    }

    internal class GooglePlacesAutocompleteAdapter(context: Context, textViewResourceId: Int) : ArrayAdapter<MyItem>(context, textViewResourceId), Filterable {
        private var resultList: List<MyItem>? = null

        override fun getCount(): Int {
            return resultList!!.size
        }

        override fun getItem(index: Int): MyItem? {
            return resultList!![index]
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filterResults = FilterResults()
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString())

                        // Assign the data to the FilterResults
                        filterResults.values = resultList
                        filterResults.count = resultList!!.size
                    }
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged()
                    } else {
                        notifyDataSetInvalidated()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun launch(cntx: Activity, objectId: Int) {
            cntx.startActivity(Intent(cntx, RouteActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_OBJECT_ID, objectId))
        }
        @SuppressLint("LongLogTag")
        fun autocomplete(input: String): List<MyItem>? {
            return ServiceGenerator.serverApi.searchMapObjectsByName(input).blockingGet()?.data?.items?.toList()
        }
    }

}

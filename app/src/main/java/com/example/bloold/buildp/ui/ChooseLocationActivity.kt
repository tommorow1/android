package com.example.bloold.buildp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.IntentHelper
import com.example.bloold.buildp.components.NetworkActivity
import com.example.bloold.buildp.databinding.ActivityChooseLocationBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import java.util.*

class ChooseLocationActivity : NetworkActivity(), OnMapReadyCallback, PlaceSelectionListener, GoogleMap.OnCameraIdleListener {
    private lateinit var mBinding: ActivityChooseLocationBinding
    private var mapInitialized=false
    private var latitude: Double?=null
    private var longitude: Double?=null
    private lateinit var map:GoogleMap
    private lateinit var placeAutocompleteFragment: PlaceAutocompleteFragment
    private var address: String?=null

    companion object {
        private const val PERMISSIONS_LOCATION = 234
        private const val REQUEST_RESOLVE_SERVICES_ERROR = 125
        val REQUEST_CHOOSE_LOCATION = 345

        fun launch(act: Activity, requestCode:Int, latitude: Double?, longitude: Double?) {
            act.startActivityForResult(Intent(act, ChooseLocationActivity::class.java)
                    .putExtra(IntentHelper.EXTRA_LATITUDE, latitude)
                    .putExtra(IntentHelper.EXTRA_LONGITUDE, longitude), requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_choose_location)
        mBinding.listener=this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        latitude=intent.getSerializableExtra(IntentHelper.EXTRA_LATITUDE) as? Double
        longitude=intent.getSerializableExtra(IntentHelper.EXTRA_LONGITUDE) as? Double
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when {
        item.itemId==R.id.action_done -> {
            setResult(Activity.RESULT_OK,
                    Intent().putExtra(IntentHelper.EXTRA_ADDRESS, address)
                            .putExtra(IntentHelper.EXTRA_LATITUDE, latitude)
                            .putExtra(IntentHelper.EXTRA_LONGITUDE, longitude))
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        val resCode=GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        when(resCode)
        {
            ConnectionResult.SUCCESS -> checkGPSPermissions()
            else ->
            {
                if(GoogleApiAvailability.getInstance().isUserResolvableError(resCode))
                    GoogleApiAvailability.getInstance().getErrorDialog(this, resCode, REQUEST_RESOLVE_SERVICES_ERROR).show()
                else Toast.makeText(this, R.string.google_services_error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkGPSPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_LOCATION)
        } else
            initMap()
    }

    private fun initMap() {
        if (!mapInitialized) {
            mapInitialized = true
            (fragmentManager.findFragmentById(R.id.fMap) as MapFragment).getMapAsync(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap()
                } else {
                    AlertDialog.Builder(this)
                            .setMessage(R.string.need_gps_permission)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok) { _, _ -> checkGPSPermissions() }
                            .setNegativeButton(android.R.string.cancel) { _, _ -> this@ChooseLocationActivity.finish() }.show()
                }
                return
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        map?.let {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                it.isMyLocationEnabled=true
            this.map=map
            map.setOnCameraIdleListener(this)
            moveToLocation()

            placeAutocompleteFragment=fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
            placeAutocompleteFragment.setOnPlaceSelectedListener(this)

        }
    }
    private fun moveToLocation()
    {
        if (latitude != null && longitude != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude!!, longitude!!), 15f))
        }
    }

    /*** Следим за перемещением карты */
    override fun onCameraIdle() {
        try {
            val addresses = Geocoder(this, Locale.getDefault())
                    .getFromLocation(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude, 1)
            if(!addresses.isEmpty()) {
                val address=addresses[0]
                if(latitude!=address.latitude||
                        longitude!=address.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(address.latitude, address.longitude)))

                latitude=address.latitude
                longitude=address.longitude
                placeAutocompleteFragment.setText(addresses[0].getAddressLine(0))
                fetchAddressData(address)
            }
            else placeAutocompleteFragment.setText("")
        }
        catch (ex: Exception)
        {
            ex.printStackTrace()
            placeAutocompleteFragment.setText("")
        }
    }
    private fun fetchAddressData(addressData:Address?)
    {
        addressData?.let { address=it.getAddressLine(0) }
    }

    /*** Выбор места в поле ввода */
    override fun onPlaceSelected(place: Place?)
    {
        place?.let {
            try {
                fetchAddressData(Geocoder(this, Locale.getDefault()).getFromLocation(it.latLng.latitude, it.latLng.longitude, 1)
                        .firstOrNull())
                latitude=it.latLng.latitude
                longitude=it.latLng.longitude
                moveToLocation()
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
    }
    override fun onError(status: Status?) {
    }

}
package com.example.bloold.buildp.ui

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.example.bloold.buildp.R
import com.example.bloold.buildp.common.Settings
import com.example.bloold.buildp.databinding.ActivityMapRouteBinding
import com.example.bloold.buildp.utils.PermissionUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


/**
 * Created by sagus on 20.11.2017.
 */
class RouteMapActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMapRouteBinding
    private var googleMap: GoogleMap?=null

    private var latFrom:Double=0.0
    private var lngFrom:Double=0.0
    private var latTo:Double=0.0
    private var lngTo:Double=0.0
    private var drawnRoute:Polyline?=null
    private var routeMode:String=TransportMode.DRIVING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this, R.layout.activity_map_route)
        mBinding.listener=this
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        latFrom=intent.getDoubleExtra(EXTRA_LAT_FROM,0.0)
        lngFrom=intent.getDoubleExtra(EXTRA_LNG_FROM,0.0)
        latTo=intent.getDoubleExtra(EXTRA_LAT_TO,0.0)
        lngTo=intent.getDoubleExtra(EXTRA_LNG_TO,0.0)

        updateRouteMode()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        val resCode= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        when(resCode)
        {
            ConnectionResult.SUCCESS -> setupMap()
            else ->
            {
                if(GoogleApiAvailability.getInstance().isUserResolvableError(resCode))
                    GoogleApiAvailability.getInstance().getErrorDialog(this, resCode, REQUEST_RESOLVE_SERVICES_ERROR).show()
                else Toast.makeText(this, R.string.google_services_error, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun updateRouteMode()
    {
        mBinding.ivWalk.setColorFilter(ContextCompat.getColor(this, if(routeMode==TransportMode.WALKING) R.color.blue else android.R.color.white))
        mBinding.ivCar.setColorFilter(ContextCompat.getColor(this, if(routeMode==TransportMode.DRIVING) R.color.blue else android.R.color.white))
        mBinding.ivBus.setColorFilter(ContextCompat.getColor(this, if(routeMode==TransportMode.TRANSIT) R.color.blue else android.R.color.white))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_USER_LOCATION) {
            if (PermissionUtil.verifyPermissions(grantResults)) {

            } else {

            }
        }
    }
    private fun setupMap()
    {
        if(googleMap!=null) return

        (supportFragmentManager.findFragmentById(R.id.fMap) as SupportMapFragment).getMapAsync({
            googleMap=it
            googleMap?.let {
                /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_USER_LOCATION)
                }*/
                it.setOnMapLoadedCallback({
                    val from=LatLng(latFrom, lngFrom)
                    val to=LatLng(latTo, lngTo)
                    it.addMarker(MarkerOptions().icon(getMarkerIconFromDrawable(resources.getDrawable(R.drawable.red_marker))).position(from))
                    it.addMarker(MarkerOptions().icon(getMarkerIconFromDrawable(resources.getDrawable(R.drawable.red_marker))).position(to))

                    it.animateCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds.Builder().include(from).include(to).build(), 100))

                    getDirections()
                })
            }
        })
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor {
        val newHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30f, resources.displayMetrics)

        val proportion=newHeight/drawable.intrinsicHeight
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap((drawable.intrinsicWidth*proportion).toInt(), (drawable.intrinsicHeight*proportion).toInt(), Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, (drawable.intrinsicWidth*proportion).toInt(), (drawable.intrinsicHeight*proportion).toInt())
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    fun onWalkingClick(v: View)
    {
        routeMode=TransportMode.WALKING
        updateRouteMode()
        getDirections()
    }
    fun onDrivingClick(v: View)
    {
        routeMode=TransportMode.DRIVING
        updateRouteMode()
        getDirections()
    }
    fun onTransitClick(v: View)
    {
        routeMode=TransportMode.TRANSIT
        updateRouteMode()
        getDirections()
    }
    private fun getDirections()
    {
        mBinding.flLoading.visibility=View.VISIBLE
        GoogleDirection.withServerKey(Settings.DIRECTIONS_API_KEY)
            .from(LatLng(latFrom, lngFrom))
            .to(LatLng(latTo, lngTo))
                .language("ru")
            .transportMode(routeMode)
            .execute(object: DirectionCallback {
                override fun onDirectionFailure(t: Throwable?) {
                    mBinding.flLoading.visibility=View.GONE
                    drawnRoute?.remove()
                    mBinding.tvDistance.text=getString(R.string.no_route)
                    Toast.makeText(this@RouteMapActivity, R.string.cant_get_route, Toast.LENGTH_LONG).show()
                }

                override fun onDirectionSuccess(direction: Direction?, rawBody: String?) {
                    mBinding.flLoading.visibility=View.GONE
                    drawnRoute?.remove()
                    if(direction?.isOK==true) {
                        var totalDistanceM=0f
                        var distance=FloatArray(1)
                        var prevPoint:LatLng?=null
                        val lineOptions = PolylineOptions()
                                .width(10f)
                                .color(ContextCompat.getColor(this@RouteMapActivity, R.color.red))
                        direction.routeList?.forEach { it.legList?.forEach { it.directionPoint.forEach {
                            lineOptions.add(it)
                            prevPoint?.let { prev->
                                Location.distanceBetween(prev.latitude, prev.longitude, it.latitude, it.longitude, distance)
                                totalDistanceM+=distance[0]
                            }
                            prevPoint=it
                        } } }
                        val distanceStr = if(totalDistanceM<1000) getString(R.string.meters, totalDistanceM.toInt()) else getString(R.string.km, totalDistanceM.div(1000).toInt())
                        val duration=direction.routeList[0].legList[0].duration.text
                        mBinding.tvDistance.text=distanceStr+", "+duration
                        drawnRoute=googleMap?.addPolyline(lineOptions)
                    } else {
                        mBinding.tvDistance.text=getString(R.string.no_route)
                        Toast.makeText(this@RouteMapActivity, R.string.cant_get_route, Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    companion object {
        private val EXTRA_LAT_FROM="latFrom"
        private val EXTRA_LAT_TO="latTo"
        private val EXTRA_LNG_FROM="lngFrom"
        private val EXTRA_LNG_TO="lngTo"
        private val REQUEST_RESOLVE_SERVICES_ERROR = 125
        private val REQUEST_USER_LOCATION = 128
        fun launch(cntx: Activity, latFrom:Double,lngFrom:Double, latTo:Double, lngTo:Double) {
            cntx.startActivity(Intent(cntx, RouteMapActivity::class.java)
                    .putExtra(EXTRA_LAT_FROM, latFrom)
                    .putExtra(EXTRA_LNG_FROM, lngFrom)
                    .putExtra(EXTRA_LAT_TO, latTo)
                    .putExtra(EXTRA_LNG_TO, lngTo))
        }
    }
}

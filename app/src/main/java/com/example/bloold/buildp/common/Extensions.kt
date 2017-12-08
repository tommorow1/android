package com.example.bloold.buildp.common

import com.google.android.gms.maps.model.LatLng

/**
 * Created by sagus on 19.11.2017.
 */
fun String?.toLocation(): LatLng?
{
    if(this!=null)
    {
        val coords=this.split(",")
        if (coords.size==2) return LatLng(coords[0].toDouble(), coords[1].toDouble())
    }
    return null
}
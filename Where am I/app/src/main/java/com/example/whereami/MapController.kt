package com.example.whereami

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapController(val map:GoogleMap) {

    fun setMapClick(sound:SoundManager) {
        map.setOnMapClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
            )
            sound.playSound()
        }
    }
    fun changeMapView(lat : Double, lon : Double,address : String){
        val mylocation = LatLng(lat,lon)
        map.addMarker(MarkerOptions().position(mylocation).title(address))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 10.0f))
        map.uiSettings.isZoomControlsEnabled = true
    }
}
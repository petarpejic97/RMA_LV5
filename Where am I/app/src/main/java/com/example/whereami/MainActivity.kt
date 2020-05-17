package com.example.whereami

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.location.Geocoder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import java.util.*
import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.os.Environment.getRootDirectory
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.*
import java.io.*
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import javax.xml.xpath.XPathConstants.STRING
import java.io.ByteArrayOutputStream as ByteArrayOutputStream1


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback{

    private var sound : Sound = Sound()

    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val locationRequestCode = 10
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder
    private lateinit var address :  MutableList<Address>
    private lateinit var map: GoogleMap
    private lateinit var fname : String

    val REQUEST_IMAGE_CAPTURE = 1
    private val PERMISSION_REQUEST_CODE: Int = 101
    private var mCurrentPhotoPath: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sound.loadSounds()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.trackLocation()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannels()

        btnSpremiFotografiju.setOnClickListener {
            if(checkPersmission())
                takePicture()
            else
                requestPermission()
        }
    }
    private val locationListener = object: LocationListener {
        override fun onProviderEnabled(provider: String?) { }
        override fun onProviderDisabled(provider: String?) { }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
        override fun onLocationChanged(location: Location?) {
            updateLocationDisplay(location)
        }
    }
    private fun updateLocationDisplay(location: Location?) {

        val lat = location!!.latitude
        val lon = location.longitude

        mygeosirina.text = "$lat"
        mygeoduzina.text = "$lon"

        setAddress(lat,lon)
    }

    @SuppressLint("SetTextI18n")
    private fun setAddress(lat : Double, lon : Double){
        geocoder = Geocoder(baseContext, Locale.getDefault())
        address = geocoder.getFromLocation(lat,lon,1)
        mydrzava.text = address[0].countryName
        mymjesto.text = address[0].locality
        myadresa.text =address[0].thoroughfare+ " " +address[0].subThoroughfare;

        fname=address[0].getAddressLine(0)
        changeMapView(lat,lon,address[0].getAddressLine(0))
    }

    private fun changeMapView(lat : Double, lon : Double,address : String){
        val mylocation = LatLng(lat,lon)
        map.addMarker(MarkerOptions().position(mylocation).title(address))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 10.0f))
        map.uiSettings.isZoomControlsEnabled = true
    }

    private fun displaySaveImageNotificaiton() {
        val file = File(mCurrentPhotoPath)
        val uri = Uri.fromFile(file)

        val intent = Intent(Intent.ACTION_VIEW) //
            .setDataAndType(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(
                    WhereAmI.ApplicationContext,
                    "com.example.android.fileprovider",
                    file!!
                ) else Uri.fromFile(file),
                "image/*"
            ).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)


        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            0
        )
        val notification = NotificationCompat.Builder(this, getChannelId(CHANNEL_LIKES))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("You took the photo!")
            .setContentText(fname)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(this)
            .notify(1001, notification)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setMapClick(map)
    }
    private fun setMapClick(map:GoogleMap) {
        map.setOnMapClickListener { latLng ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker))
            )
            sound.playSound()
        }
    }

    private fun trackLocation() {
        if(hasPermissionCompat(locationPermission)){
            startTrackingLocation()
        } else {
            requestPermisionCompat(arrayOf(locationPermission), locationRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode){
            locationRequestCode -> {
                if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    trackLocation()
                else
                    Toast.makeText(this, "permision denied", Toast.LENGTH_SHORT).show()
            }

            PERMISSION_REQUEST_CODE->{
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    takePicture()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.android.fileprovider",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            //To get the File for further usage
            val auxFile = File(mCurrentPhotoPath)
            var bitmap :Bitmap=BitmapFactory.decodeFile(mCurrentPhotoPath)

            displaySaveImageNotificaiton()
        }
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
    }

    @Throws(IOException::class)
    private fun createFile(): File {

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    private fun startTrackingLocation() {
        Log.d("TAG", "Tracking location")
        val criteria: Criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        val provider = locationManager.getBestProvider(criteria, true)
        val minTime = 1000L
        val minDistance = 10.0F
        try{
            locationManager.requestLocationUpdates(provider, minTime, minDistance, locationListener)
        } catch (e: SecurityException){
            Toast.makeText(this, "permision denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locationListener)
    }

    /*private fun loadSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.mSoundPool = SoundPool.Builder().setMaxStreams(10).build()
        } else {
            this.mSoundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        }
        this.mSoundPool.setOnLoadCompleteListener { _, _, _ -> mLoaded = true }
        this.mSoundMap[R.raw.marker] = this.mSoundPool.load(this, R.raw.marker, 1)
    }

    fun playSound() {
        val soundID = this.mSoundMap[R.raw.marker] ?: 0
        this.mSoundPool.play(soundID, 1f, 1f, 1, 0, 1f)
    }*/
}

package com.example.whereami

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun AppCompatActivity.hasPermissionCompat (permission: String): Boolean{
    return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
fun AppCompatActivity.shouldShowRationaleCompat(permission: String): Boolean{
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}
fun AppCompatActivity.requestPermisionCompat(permission: Array<String>, requestCode: Int){
    ActivityCompat.requestPermissions(this, permission, requestCode)
}
fun AppCompatActivity.checkPersmission(): Boolean {
    return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
}

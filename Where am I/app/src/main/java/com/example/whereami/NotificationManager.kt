package com.example.whereami

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import java.io.File

class NotificationManager(var mCurrentPhotoPath : String) {

    private lateinit var notificationText : String

    fun setNotificationTitle(title : String){
        notificationText = title
    }
    fun displaySaveImageNotificaiton() {
        val file = File(mCurrentPhotoPath)

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
            WhereAmI.ApplicationContext,
            0,
            intent,
            0
        )
        val notification = NotificationCompat.Builder(WhereAmI.ApplicationContext, getChannelId(CHANNEL_LIKES))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("You took the photo!")
            .setContentText(notificationText)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(WhereAmI.ApplicationContext)
            .notify(1001, notification)
    }
}
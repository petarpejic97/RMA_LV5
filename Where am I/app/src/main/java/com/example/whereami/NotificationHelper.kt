package com.example.whereami

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import com.example.whereami.WhereAmI.Companion.ApplicationContext
import kotlin.coroutines.coroutineContext

fun getChannelId(name: String): String = "${WhereAmI.ApplicationContext.packageName}-$name"
const val CHANNEL_LIKES = "Likes_Channel"
const val COMMENTS_CHANNEL = "Comments_Channel"
@RequiresApi(api = Build.VERSION_CODES.O)
fun createNotificationChannel(name: String, description: String, importance: Int): NotificationChannel {
    val channel = NotificationChannel(getChannelId(name), name, importance)
    channel.description = description
    channel.setShowBadge(true)
    return channel
}
@RequiresApi(api = Build.VERSION_CODES.O)
fun createNotificationChannels() {
    val channels = mutableListOf<NotificationChannel>()
    channels.add(createNotificationChannel(
        CHANNEL_LIKES,
        "Likes on your posts",
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    ))
    channels.add(createNotificationChannel(
        COMMENTS_CHANNEL,
        "Comments on your posts",
        NotificationManagerCompat.IMPORTANCE_HIGH
    ))
    val notificationManager = WhereAmI.ApplicationContext.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannels(channels)
}

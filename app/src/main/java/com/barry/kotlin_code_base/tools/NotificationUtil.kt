package com.barry.kotlin_code_base.tools

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.barry.kotlin_code_base.R
import com.bumptech.glide.Glide


class NotificationUtil(private val context: Context) {

    private val FCM_CHANNEL: String = "fcm_notification_channel"

    private val NOTIFICATION_GROUP: String = "notification_group"

    private val notificationManager: NotificationManager  = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        initNotificationChannel()
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(FCM_CHANNEL, "通知", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun postFCMNotification(title: String, body: String, url: String?, intent: Intent) {
        val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationCompat.Builder(context, FCM_CHANNEL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setLargeIcon(getImage(url))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setOngoing(false)
                .setGroup(NOTIFICATION_GROUP);
        } else {
            NotificationCompat.Builder(context, FCM_CHANNEL)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setLargeIcon(getImage(url))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT))
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setOngoing(false)
                .setGroup(NOTIFICATION_GROUP);
        }
        val notification = notificationBuilder.build()
        val id: Int = SystemClock.uptimeMillis().toInt()
        notificationManager.notify(id, notification)
    }

    private fun getImage(url: String?): Bitmap? {
        if (!url.isNullOrEmpty()) {
            try {
                return Glide.with(context).asBitmap().load(url).submit().get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

}
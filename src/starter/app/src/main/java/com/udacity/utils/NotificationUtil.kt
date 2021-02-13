package com.udacity.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, fileDescription: String, downloadStatus: Boolean) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    contentIntent.putExtra("fileDescription", fileDescription)
    contentIntent.putExtra("downloadStatus", downloadStatus)

    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    var builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.general_notification_channel_id)
    )
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                    R.drawable.ic_assistant_black_24dp,
                    applicationContext.getString(R.string.notification_detail_button),
                    contentPendingIntent
            )

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}
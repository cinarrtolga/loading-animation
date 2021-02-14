package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.utils.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*

class MainActivity : AppCompatActivity() {
    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private var selectedRadio = ""
    private var TITLE = ""
    private var URL = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        radio_options.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            selectedRadio = when (checkedId) {
                R.id.radio_glide -> {
                    "Glide"
                }
                R.id.radio_loadapp -> {
                    "LoadApp"
                }
                R.id.radio_retrofit -> {
                    "Retrofit"
                }
                else -> ""
            }

            contentDecider()
        })

        custom_button.setOnClickListener {
            if (selectedRadio.isEmpty()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.option_required),
                    Toast.LENGTH_SHORT
                ).show()
                custom_button.completeAnimation()
            } else {
                download()
            }
        }

        createChannel(
            getString(R.string.general_notification_channel_id),
            getString(R.string.general_notification_channel)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            custom_button.completeAnimation()

            var statusFlag = false
            val query = DownloadManager.Query().setFilterById(id!!)
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.query(query).use { cursor ->
                if (cursor.moveToFirst()) {
                    val downloadStatus =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                    if(downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                        statusFlag = true
                    } else {
                        statusFlag = false
                    }
                }
            }

            Toast.makeText(
                context,
                resources.getString(R.string.download_success),
                Toast.LENGTH_SHORT
            ).show()
            notificationManager.sendNotification(
                resources.getString(R.string.download_success),
                applicationContext,
                TITLE,
                statusFlag
            )
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download completed"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun contentDecider() {
        when (selectedRadio) {
            "Glide" -> {
                TITLE = resources.getString(R.string.glide_text)
                URL = "https://github.com/bumptech/glide/archive/master.zip"
            }
            "LoadApp" -> {
                TITLE = resources.getString(R.string.load_app_text)
                URL =
                    "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
            }
            "Retrofit" -> {
                TITLE = resources.getString(R.string.retrofit_text)
                URL = "https://github.com/square/retrofit/archive/master.zip"
            }
        }
    }
}

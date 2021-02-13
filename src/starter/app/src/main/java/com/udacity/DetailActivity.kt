package com.udacity

import android.app.DownloadManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val intent = intent

        intent.getStringExtra("fileDescription")?.let {
            file_description_text.text = it
        }
        intent.getBooleanExtra("downloadStatus", false)?.let {
            when {
                it-> {
                    status_description_text.text = "Success"
                    status_description_text.setTextColor(Color.GREEN)
                }
                else -> {
                    status_description_text.text = "Fail"
                    status_description_text.setTextColor(Color.RED)
                }
            }
        }

        ok_button.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}

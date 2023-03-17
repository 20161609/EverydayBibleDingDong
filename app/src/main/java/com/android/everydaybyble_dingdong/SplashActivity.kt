package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val intent : Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadcastReceiver(Mainactivity : Activity) : BroadcastReceiver() {
    val Mainactivity : Activity = Mainactivity

    override fun onReceive(context: Context?, intent: Intent?) {
        // function to be executed at midnight
    }
}

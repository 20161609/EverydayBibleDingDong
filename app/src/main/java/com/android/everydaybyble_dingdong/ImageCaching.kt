package com.android.everydaybyble_dingdong

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun ImageCaching(imageView : ImageView, pictureIndex : Int){
    val storage = Firebase.storage
    val storageRef = storage.reference
    val islandRef = storageRef.child(daySelection[pictureIndex].fileName)//arrayFileNames[pictureIndex])

    islandRef.downloadUrl.addOnSuccessListener { uri ->
        Glide.with(imageView.context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(imageView)
    }.addOnFailureListener {
        // Handle any errors
        Log.e("getBytes","failure")
    }
}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

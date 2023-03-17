package com.android.everydaybyble_dingdong

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

fun imageCaching(imageView : ImageView, pictureIndex : Int){
    val storage = Firebase.storage
    val storageRef = storage.reference
    val islandRef = storageRef.child(daySelection[pictureIndex].fileName)

    imageBox?.addView(o_animation)
    islandRef.downloadUrl.addOnSuccessListener { uri ->
        Glide.with(imageView.context)
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .centerCrop()
            .into(imageView).run {
                imageBox?.removeView(o_animation)
            }
        Log.e("getBytes","Success")
    }.addOnFailureListener {
        // Handle any errors
        imageBox?.removeView(o_animation)
        Log.e("getBytes","failure")
    }

}

fun isNetworkConnected(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

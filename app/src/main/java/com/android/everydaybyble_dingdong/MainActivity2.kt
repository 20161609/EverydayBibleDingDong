package com.android.everydaybyble_dingdong

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val g = findViewById<ImageView>(R.id.glide);
        val storage = Firebase.storage
        val storageRef = storage.reference
        val islandRef = storageRef.child("20230117" + ".jpeg")
        val ONE_MEGABYTE: Long = 1024 * 1024

        var shareUri : Uri
        var sex : Int = 123

        islandRef.downloadUrl.addOnSuccessListener { uri ->
            Log.e("FUCK",islandRef.toString())
            shareUri = uri
            Glide.with(g.context)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .centerCrop()
                .into(g)
        }.addOnFailureListener {
            // Handle any errors
            Log.e("getBytes","failure")
        }

        val button1 : Button = findViewById(R.id.button1)
        button1.setOnClickListener(){
            islandRef.downloadUrl.addOnSuccessListener { uri ->
                val shareIntent : Intent = Intent().apply{
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "image/jpeg"
                }
                startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.app_name)))
                Log.e("공유","성공")
            }.addOnFailureListener {
                // Handle any errors
                Log.e("getBytes","failure")
            }

            
            //val saveimagefile = SaveImagefile(g.context)            saveimagefile.saveImage(g, this)
        }
    }
}
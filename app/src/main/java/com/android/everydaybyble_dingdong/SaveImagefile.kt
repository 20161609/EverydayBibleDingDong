package com.android.everydaybyble_dingdong

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.util.*


class SaveImageFile(context: Context) {
    public var filename : String
    init{
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val todayDate = year*10000 + month*100 + day

        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        val s = calendar.get(Calendar.SECOND)
        val totalSecond = m*60 + s
        filename=todayDate.toString() +
                (calendar.get(Calendar.MILLISECOND)+totalSecond).toString() +
                "_dingdong"
    }
    private fun getBitmapFromView(view: ImageView): Bitmap {
        return Bitmap.createBitmap(view.width, view.height,Bitmap.Config.ARGB_8888).apply {
            Canvas(this).apply {
                view.draw(this)
            }
        }
    }

    fun saveImage_v10(itemImage: ImageView, activity: Activity) {
        Log.e("saveImage_v10", "Excuted")
        val imageFromView = getBitmapFromView(itemImage)
        val resolver = activity.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        imageUri?.let {
            resolver.openOutputStream(it).use { outputStream ->
                imageFromView.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            Log.e("Save", "success")
        } ?: run {
            Toast.makeText(activity, "Unable to save image", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImage_v(itemImage: ImageView, activity: Activity) {
        val imageFromView = getBitmapFromView(itemImage)
        val imageDir = File("${activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ChatOut")
        imageDir.mkdirs()
        val imageFile = File("${imageDir.absolutePath}/$filename.jpg")

        if (!imageFile.exists()) {
            val contentResolver = ContentValues().apply {
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
            }

            activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentResolver)?.let { uri ->
                activity.contentResolver.openOutputStream(uri)?.let { outputStream ->
                    imageFromView.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.close()
                }
            }

            Log.e("Save", "success")
        } else {
            Toast.makeText(activity, "Already saved", Toast.LENGTH_SHORT).show()
        }
    }


    public fun saveImage_v11(itemImage: ImageView, activity: Activity, filename : String) {
        Log.e("saveImage_v11", "Excuted")
        val imageFromView = getBitmapFromView(itemImage)

        ByteArrayOutputStream().apply {
            imageFromView.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }

        val imageFile =  File("${activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ChatOut/$filename.jpg/")
        if (!imageFile.exists()) {

            val contentResolver = ContentValues().apply {
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
            }

            activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentResolver).apply {
                imageFromView.compress(Bitmap.CompressFormat.JPEG, 100, activity.contentResolver.openOutputStream(this!!))
            }

            Log.e("Save", "success")
        }
        else{
            Toast.makeText(activity, "Already saved", Toast.LENGTH_SHORT).show()
        }
    }

    /* Checks if external storage is available for read and write */
    public fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    /* Checks if external storage is available to at least read */
    public fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }
}
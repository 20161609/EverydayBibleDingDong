package com.android.everydaybyble_dingdong

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import java.io.ByteArrayOutputStream

fun shareImage_v10(imageView: ImageView, context: Context) {
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap

    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    uri?.let { uri ->
        context.contentResolver.openOutputStream(uri).use { outputStream ->
            outputStream?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}

fun shareImage_v11(imageView: ImageView, context: Context) {
    imageView.isDrawingCacheEnabled = true
    val bitmap = Bitmap.createBitmap(imageView.drawingCache)
    imageView.isDrawingCacheEnabled = false

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/*"

    fun getImageUri(context: Context, image: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
        return Uri.parse(path)
    }
    val uri = getImageUri(context, bitmap)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    context.startActivity(Intent.createChooser(intent,"Share Image"))
}


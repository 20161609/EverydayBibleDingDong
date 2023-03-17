package com.android.everydaybyble_dingdong
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import java.io.OutputStream

// Function to save the ImageView image to the gallery
fun saveImageToGallery(context: Context, imageView: ImageView, title: String, description: String) {
    // Get the bitmap from the ImageView
    val bitmap = (imageView.drawable as BitmapDrawable).bitmap

    // Get the content resolver
    val contentResolver = context.contentResolver

    // Create a ContentValues object
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, title)
        put(MediaStore.Images.Media.DESCRIPTION, description)
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    }

    // Insert the image into the MediaStore
    val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    if (uri != null) {
        // Open an OutputStream to the MediaStore entry
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)

        if (outputStream != null) {
            // Save the bitmap to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()

            // Notify the MediaScanner that the image has been added
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, title, description)
        }
    }
}

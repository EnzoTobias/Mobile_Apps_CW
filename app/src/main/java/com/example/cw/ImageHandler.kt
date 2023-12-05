package com.example.cw
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.graphics.get
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageHandler {
    companion object {
        const val REQUEST_IMAGE_PICK = 1001


        fun pickImagesFromGallery(activity: Activity, multiple: Boolean = true) {
            val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickIntent.type = "image/*"
            pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple)
            activity.startActivityForResult(
                Intent.createChooser(pickIntent, "Select Picture"),
                REQUEST_IMAGE_PICK
            )
        }

        fun handleImagePickerResult(context: Context,data: Intent?): String {
            val imagePaths = mutableListOf<String>()

            if (data == null) return ""

            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    val imagePath = saveImageToInternalStorage(context,imageUri)
                    imagePath?.let { imagePaths.add(it) }
                }
            } else if (data.data != null) {
                val imageUri = data.data!!
                val imagePath = saveImageToInternalStorage(context,imageUri)
                imagePath?.let { imagePaths.add(it) }
            }

            return imagePaths.joinToString(separator = ";")
        }

        private fun saveImageToInternalStorage(context: Context, uri: android.net.Uri): String? {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir: File? =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val contentResolver = context.contentResolver

            try {
                val file = File.createTempFile(imageFileName, ".jpg", storageDir)
                val destinationUri = Uri.fromFile(file)

                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = contentResolver.openOutputStream(destinationUri)

                inputStream?.use { input ->
                    outputStream?.use { output ->
                        input.copyTo(output)
                    }
                }

                return destinationUri.path
            } catch (e: IOException) {
                Log.e("ImageHandler", "Error saving image: ${e.message}")
            }
            return null
        }

        fun saveImageToInternalStorageFromUrl(
            context: Context,
            imageUrl: String,
            callback: (String?) -> Unit
        ) {
            val requestQueue = Volley.newRequestQueue(context)
            val imageRequest = ImageRequest(
                imageUrl,
                { response ->
                    val fileName = "image_${System.currentTimeMillis()}.jpg"
                    val directory = File(context.filesDir, "images")

                    if (!directory.exists()) {
                        directory.mkdirs()
                    }

                    val file = File(directory, fileName)
                    try {
                        val fileOutputStream = FileOutputStream(file)
                        response?.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        fileOutputStream.flush()
                        fileOutputStream.close()

                        callback.invoke(file.absolutePath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback.invoke(null)
                    }
                },
                0,
                0,
                null,
                null,
                { error: VolleyError? ->
                    error?.printStackTrace()
                    callback.invoke(null)
                })

            requestQueue.add(imageRequest)
        }
    }
}
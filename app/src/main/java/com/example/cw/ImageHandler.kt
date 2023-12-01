package com.example.cw
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
class ImageHandler {

    companion object {
        const val REQUEST_IMAGE_PICK = 1001


        fun pickImagesFromGallery(activity: Activity) {
            val pickIntent = Intent(Intent.ACTION_GET_CONTENT)
            pickIntent.type = "image/*"
            pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
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
            val storageDir: File? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            } else {
                Environment.getExternalStorageDirectory()
            }
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
    }
}
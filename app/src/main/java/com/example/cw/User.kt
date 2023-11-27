package com.example.cw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

class User(val username: String, val userID: Int, val imagePath: String, val password: String) {
    fun getUserPfpFromPath(context: Context, @DrawableRes defaultImageResId: Int): Bitmap {
        return try {
            val inputStream = context.assets.open(imagePath)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, defaultImageResId)
        }
    }
}
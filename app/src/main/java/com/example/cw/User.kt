package com.example.cw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

class User(var username: String, val userID: String, var imagePath: String) {
    fun getUserPfpFromPath(context: Context, @DrawableRes defaultImageResId: Int): Bitmap {
        return try {
            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, defaultImageResId)
        }
    }
}
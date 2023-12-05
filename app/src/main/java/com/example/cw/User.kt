package com.example.cw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

class User(val username: String, val userID: Int, var imagePath: String, val password: String) {
    fun getUserPfpFromPath(context: Context, @DrawableRes defaultImageResId: Int): Bitmap {
        return try {
            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, defaultImageResId)
        }
    }
}
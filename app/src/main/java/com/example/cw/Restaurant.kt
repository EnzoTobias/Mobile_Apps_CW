package com.example.cw
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import java.io.InputStream
import java.net.URL





class Restaurant(var name: String, var restaurantID: Int, var imagePath: String, var description: String) {

    fun getRestaurantImageFromPath(context: Context, @DrawableRes defaultImageResId: Int): Bitmap {
        return try {
            BitmapFactory.decodeFile(imagePath)

        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, defaultImageResId)
        }
    }

}
package com.example.cw
import android.widget.ImageView
import android.graphics.drawable.Drawable
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes

class Restaurant(var name: String, var restaurantID: Int, var imagePath: String, var description: String) {

    fun getRestaurantImageFromPath(context: Context, @DrawableRes defaultImageResId: Int): Bitmap {
        return try {
            val inputStream = context.assets.open(imagePath)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, defaultImageResId)
        }
    }

}
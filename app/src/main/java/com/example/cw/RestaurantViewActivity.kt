package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class RestaurantViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_view)
        val restaurantImage: ImageView = findViewById(R.id.ImageRestaurant)
        val restaurantName: TextView = findViewById(R.id.restaurantName)
        val restaurantDesc: TextView = findViewById(R.id.restaurantDesc)
        val starImage1: ImageView = findViewById(R.id.starImage1)
        val starImage2: ImageView = findViewById(R.id.starImage2)
        val starImage3: ImageView = findViewById(R.id.starImage3)
        val starImage4: ImageView = findViewById(R.id.starImage4)
        val starImage5: ImageView = findViewById(R.id.starImage5)
        val receivedIntent = intent
        val receivedResID = receivedIntent.getIntExtra("RESTAURANT_ID", 0)
        val db = AppDatabase(this)
        val restaurant: Restaurant
        restaurant = db.getRestaurantById(receivedResID)


        restaurantName.text = restaurant.name
        restaurantDesc.text = restaurant.description
        restaurantImage.setImageBitmap(restaurant.getRestaurantImageFromPath(this, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
        val restaurantScore = db.getRestaurantScore(restaurant)
        Review.displayStars(restaurantScore, starImage1, starImage2, starImage3, starImage4, starImage5)


    }
}
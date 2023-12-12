package com.example.cw

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.auth.FirebaseAuth

class RestaurantViewActivity : BasicActivity() {
    var receivedResID: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        val restaurantImage: ImageView = findViewById(R.id.ImageRestaurant)
        val restaurantName: TextView = findViewById(R.id.restaurantName)
        val restaurantDesc: TextView = findViewById(R.id.restaurantDesc)
        val starImage1: ImageView = findViewById(R.id.starImage1)
        val starImage2: ImageView = findViewById(R.id.starImage2)
        val starImage3: ImageView = findViewById(R.id.starImage3)
        val starImage4: ImageView = findViewById(R.id.starImage4)
        val starImage5: ImageView = findViewById(R.id.starImage5)
        val mainLinear = findViewById<LinearLayout>(R.id.mainLinear)
        val reviewButton = findViewById<Button>(R.id.reviewButton)
        val receivedIntent = intent
        receivedResID = receivedIntent.getIntExtra("RESTAURANT_ID", 0)
        val db = AppDatabase(this)
        val restaurant: Restaurant
        restaurant = db.getRestaurantById(receivedResID)

        restaurantName.text = restaurant.name
        restaurantDesc.text = restaurant.description
        restaurantImage.setImageBitmap(restaurant.getRestaurantImageFromPath(this, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
        val restaurantScore = db.getRestaurantScore(restaurant)
        Review.displayStars(restaurantScore, starImage1, starImage2, starImage3, starImage4, starImage5)
        Review.displayReviewsInLinearLayout(db.reviewsByRestaurant(receivedResID),this, mainLinear)



        reviewButton.setOnClickListener() {
            val mAuth = FirebaseAuth.getInstance()
            val currentUserFire = mAuth.currentUser
            if (currentUserFire == null) {
                val intent = Intent(this, Login::class.java)
                ContextCompat.startActivity(this, intent, null)
            } else {
                val intent = Intent(this, CreateReviewActivity::class.java)
                intent.putExtra("RESTAURANT_ID", restaurant.restaurantID)
                intent.putExtra("USER_ID", "")
                intent.putExtra("CREATE_OR_EDIT", true)
                ContextCompat.startActivity(this, intent, null)
            }

        }




    }

    override fun onBackPressed() {
        val intent = Intent(this, RestaurantListActivity::class.java)
        startActivity(intent)
    }
    override fun getLayoutID(): Int {
        return R.layout.activity_restaurant_view
    }


}
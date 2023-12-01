package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class CreateReviewActivity : AppCompatActivity() {

    private lateinit var reviewInput: EditText
    private lateinit var submitReview: Button
    private lateinit var starImage1: ImageView
    private lateinit var starImage2: ImageView
    private lateinit var starImage3: ImageView
    private lateinit var starImage4: ImageView
    private lateinit var starImage5: ImageView
    private var receivedResID: Int = 0
    private var receivedUserID: Int = 0
    private lateinit var db: AppDatabase
    private lateinit var review: Review

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_review)

        reviewInput = findViewById(R.id.reviewInput)
        submitReview = findViewById(R.id.submitReviewButton)
        starImage1 = findViewById(R.id.starImage1)
        starImage2 = findViewById(R.id.starImage2)
        starImage3 = findViewById(R.id.starImage3)
        starImage4 = findViewById(R.id.starImage4)
        starImage5 = findViewById(R.id.starImage5)
        db = AppDatabase(this)

        val receivedIntent = intent
        receivedResID = receivedIntent.getIntExtra("RESTAURANT_ID", 0)
        receivedUserID = receivedIntent.getIntExtra("USER_ID", 0)
        val restaurant = db.getRestaurantById(receivedResID)
        val user = db.getUserById(receivedUserID)
        review = Review("", db.getFreeReviewID(), restaurant,1, user)

        reviewInput.setText(review?.text ?: "")
        Review.displayStars(review.rating.toDouble(), starImage1, starImage2, starImage3, starImage4, starImage5)

        starImage1.setOnClickListener { setRatingAndDisplayStars(1) }
        starImage2.setOnClickListener { setRatingAndDisplayStars(2) }
        starImage3.setOnClickListener { setRatingAndDisplayStars(3) }
        starImage4.setOnClickListener { setRatingAndDisplayStars(4) }
        starImage5.setOnClickListener { setRatingAndDisplayStars(5) }

        submitReview.setOnClickListener {
            var finish: Boolean = false
            val newReviewText = reviewInput.text.toString()

            if (newReviewText.length > 3) {
                review?.let {
                    it.text = newReviewText
                    val updated = db.addReview(review)

                    if (updated) {
                        showSnackbar("Review updated")
                        finish = true
                    } else {
                        showSnackbar("Failed to update review")
                    }
                }
            } else {
                showSnackbar("Review must be longer than 3 characters")
            }
            if (finish) {
                val intent = Intent(this, RestaurantViewActivity::class.java)
                intent.putExtra("RESTAURANT_ID", restaurant.restaurantID)
                ContextCompat.startActivity(this, intent, null)
            }
        }
    }

    private fun setRatingAndDisplayStars(rating: Int) {
        review.rating = rating
        Review.displayStars(rating.toDouble(), starImage1, starImage2, starImage3, starImage4, starImage5)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}
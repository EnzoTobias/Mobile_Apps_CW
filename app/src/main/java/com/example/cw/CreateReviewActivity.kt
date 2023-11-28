package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar

class CreateReviewActivity : AppCompatActivity() {
    private lateinit var reviewInput: EditText
    private lateinit var submitReview: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_review)
        reviewInput = findViewById(R.id.reviewInput)
        submitReview = findViewById(R.id.submitReviewButton)

        submitReview.setOnClickListener {
            val receivedIntent = intent
            val db = AppDatabase(this)
            val receivedResID = receivedIntent.getIntExtra("RESTAURANT_ID", 0)
            val restaurant = db.getRestaurantById(receivedResID)

            val currentUser = CurrentUser.currentUser
            if (currentUser == null) {
                showSnackbar("Error: no user logged in")
                return@setOnClickListener
            }

            val reviewText = reviewInput.text.toString()
            if (reviewText.length < 3) {
                showSnackbar("Review must be longer than 3 characters")
                return@setOnClickListener
            }

            val review = Review(reviewText, db.getFreeReviewID(), restaurant, 0, currentUser)
            val reviewCreated = db.addReview(review)
            if (reviewCreated) {
                showSnackbar("Review submitted")
            } else {
                showSnackbar("Error creating review")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}
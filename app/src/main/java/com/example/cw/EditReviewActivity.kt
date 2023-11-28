package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditReviewActivity : AppCompatActivity() {

    private lateinit var reviewInput: EditText
    private lateinit var submitReview: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)
        val receivedIntent = intent
        val receivedRevID = receivedIntent.getIntExtra("REVIEW_ID", 0)
        val db = AppDatabase(this)
        val review = db.getReviewById(receivedRevID)
        reviewInput = findViewById(R.id.reviewInput)
        submitReview = findViewById(R.id.submitReviewButton)

        if (review != null) {
            reviewInput.setText(review.reviewID)
        }

        submitReview.setOnClickListener {

            if (review != null) {
                review.text = reviewInput.text.toString()
            }
        }


    }
}
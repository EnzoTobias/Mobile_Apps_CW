package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class EditReviewActivity : AppCompatActivity() {

    private lateinit var reviewInput: EditText
    private lateinit var submitReview: Button
    private var receivedRevID: Int = 0
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_review)

        reviewInput = findViewById(R.id.reviewInput)
        submitReview = findViewById(R.id.submitReviewButton)
        db = AppDatabase(this)
        val receivedIntent = intent
        receivedRevID = receivedIntent.getIntExtra("REVIEW_ID", 0)
        val review = db.getReviewById(receivedRevID)
        reviewInput.setText(review?.text ?: "")

        submitReview.setOnClickListener {
            val newReviewText = reviewInput.text.toString()

            if (newReviewText.length > 3) {
                review?.let {
                    it.text = newReviewText
                    val updated = db.updateReview(it)

                    if (updated) {
                        showSnackbar("Review updated")
                    } else {
                        showSnackbar("Failed to update review")
                    }
                }
            } else {
                showSnackbar("Review must be longer than 3 characters")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }
}

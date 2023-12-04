package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class Account : BasicActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedIntent = intent
        val isReports = receivedIntent.getBooleanExtra("REPORT", false)
        val reviewsLayout = findViewById<LinearLayout>(R.id.userReviewsLayout)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val userImage = findViewById<ImageView>(R.id.usernameImage)
        val db = AppDatabase(this)
        val user = CurrentUser.currentUser!!
        val reviewList = db.reviewsByUser(user.userID)


        usernameText.text = user.username
        userImage.setImageBitmap(user.getUserPfpFromPath(this, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
        if (isReports) {
            val reportList = db.getAllReports()
            Report.displayReviewsInLinearLayout(reportList,this, reviewsLayout)
        } else {
            Review.displayReviewsInLinearLayout(reviewList,this, reviewsLayout,true)
        }
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_account
    }

    override fun onBackPressed() {
        val intent = Intent(this, RestaurantListActivity::class.java)
        startActivity(intent)
    }

}
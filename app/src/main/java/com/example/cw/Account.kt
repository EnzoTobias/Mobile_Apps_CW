package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isVisible

class Account : BasicActivity() {
    val user = CurrentUser.currentUser!!
    val db = AppDatabase(this)
    lateinit var userImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedIntent = intent
        val isReports = receivedIntent.getBooleanExtra("REPORT", false)
        val reviewsLayout = findViewById<LinearLayout>(R.id.userReviewsLayout)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val userOptions = findViewById<ImageView>(R.id.userOptions)
        userImage = findViewById<ImageView>(R.id.usernameImage)
        val reviewList = db.reviewsByUser(user.userID)


        usernameText.text = user.username
        userImage.setImageBitmap(user.getUserPfpFromPath(this, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
        if (isReports) {
            val reportList = db.getAllReports()
            Report.displayReviewsInLinearLayout(reportList,this, reviewsLayout)
            userOptions.isVisible = false
        } else {
            Review.displayReviewsInLinearLayout(reviewList,this, reviewsLayout,true)
        }

        userOptions.setOnClickListener {
            val popupMenu = PopupMenu(this, userOptions)

            popupMenu.menuInflater.inflate(R.menu.user_options_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.change_pfp_option -> {
                        ImageHandler.pickImagesFromGallery(this@Account, false)

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageHandler.REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imagePaths = ImageHandler.handleImagePickerResult(this@Account, data)
            user.imagePath = imagePaths
            db.updateUser(user)
            CurrentUser.currentUser = user
            userImage.setImageBitmap(user.getUserPfpFromPath(this, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
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


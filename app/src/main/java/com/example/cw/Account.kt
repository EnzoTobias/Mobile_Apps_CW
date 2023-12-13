package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class Account : BasicActivity() {

    lateinit var userImage: ImageView
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val receivedIntent = intent
        val isReports = receivedIntent.getBooleanExtra("REPORT", false)
        val specificUser = receivedIntent.getStringExtra("SPECIFIC_USER")
        val reviewsLayout = findViewById<LinearLayout>(R.id.userReviewsLayout)
        val usernameText = findViewById<TextView>(R.id.usernameText)
        val userOptions = findViewById<ImageView>(R.id.userOptions)
        var otherUser = false
        val fireUser = FirebaseAuth.getInstance().currentUser!!
        val db = AppDatabase(this)
        user = db.getUserById(fireUser.uid)

        if (specificUser != null) {
            user = db.getUserById(specificUser)
            otherUser = true
        }

        userImage = findViewById<ImageView>(R.id.usernameImage)
        val reviewList = db.reviewsByUser(user.userID)


        usernameText.text = user.username
        userImage.setImageBitmap(user.getUserPfpFromPath(this, R.drawable.account_empty))
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
            val changePfp = popupMenu.menu.findItem(R.id.change_pfp_option)
            val logout = popupMenu.menu.findItem(R.id.logout_option)
            val block = popupMenu.menu.findItem(R.id.block_option)

            val mAuth = FirebaseAuth.getInstance()
            val currentUserFire = mAuth.currentUser!!
            val currentUser = db.getUserById(currentUserFire.uid)
            val blocks = db.getUserBlocks(currentUser.userID)

            if (otherUser) {
                logout.isVisible = false
                changePfp.isVisible = false
                block.isVisible = true
                if (user.userID in blocks) {
                    block.setTitle(getString(R.string.unblock_option))
                }
            } else {
                logout.isVisible = true
                changePfp.isVisible = true
                block.isVisible = false
            }

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.change_pfp_option -> {
                        ImageHandler.pickImagesFromGallery(this@Account, false)

                        true
                    }

                    R.id.logout_option -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, RestaurantListActivity::class.java)
                        ContextCompat.startActivity(this, intent, null)
                        true
                    }

                    R.id.block_option -> {
                        if (user.userID in blocks) {
                            db.removeBlock(currentUser.userID,user.userID)
                        } else {
                            db.addBlockedUser(currentUser.userID,user.userID)
                        }
                        val intent = Intent(this, Account::class.java)
                        intent.putExtra("SPECIFIC_USER", user.userID)
                        this.startActivity(intent)
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
        val db = AppDatabase(this)
        if (requestCode == ImageHandler.REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imagePaths = ImageHandler.handleImagePickerResult(this@Account, data)
            user.imagePath = imagePaths
            db.updateUser(user)
            userImage.setImageBitmap(user.getUserPfpFromPath(this, R.drawable.account_empty))
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


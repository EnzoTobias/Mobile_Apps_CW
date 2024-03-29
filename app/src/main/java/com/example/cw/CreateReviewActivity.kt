package com.example.cw

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class CreateReviewActivity : AppCompatActivity() {

    private lateinit var reviewInput: EditText
    private lateinit var submitReview: Button
    private lateinit var starImage1: ImageView
    private lateinit var starImage2: ImageView
    private lateinit var starImage3: ImageView
    private lateinit var starImage4: ImageView
    private lateinit var starImage5: ImageView
    private var receivedResID: Int = 0
    private var receivedUserID: String = ""
    private var receiveCreateOrEdit: Boolean = true
    private var receivedRevID: Int = 0
    private lateinit var db: AppDatabase
    private lateinit var review: Review
    private lateinit var uploadImagesButton: Button
    private lateinit var imagesLinear: LinearLayout
    private var fromUserPage: Boolean = false
    var restaurant: Restaurant = Restaurant("",0,"", "")

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
        uploadImagesButton = findViewById(R.id.uploadImagesButton)
        imagesLinear = findViewById(R.id.imagesLinear)
        val actionName = findViewById<TextView>(R.id.textView15)
        val user: User

        db = AppDatabase(this)

        val receivedIntent = intent
        fromUserPage = receivedIntent.getBooleanExtra("FROM_USER", false)
        receiveCreateOrEdit = receivedIntent.getBooleanExtra("CREATE_OR_EDIT", true)
        if (receiveCreateOrEdit) {
            receivedResID = receivedIntent.getIntExtra("RESTAURANT_ID", 0)
            receivedUserID = receivedIntent.getStringExtra("USER_ID")!!
            restaurant = db.getRestaurantById(receivedResID)
            user = db.getUserById(receivedUserID)
            val mAuth = FirebaseAuth.getInstance()
            val currentUser = db.getUserById(mAuth.currentUser!!.uid)
            review = Review("", db.getFreeReviewID(), restaurant,5, currentUser, "")
        } else {
            receivedRevID = receivedIntent.getIntExtra("REVIEW_ID", 0)
            review = db.getReviewById(receivedRevID)!!
            restaurant = review!!.restaurant
            user = review.user
            reviewInput.setText(review?.text ?: "")
            Review.displayStars(review.rating.toDouble(), starImage1, starImage2, starImage3, starImage4, starImage5)
            Review.displayReviewImagesInLinearLayout(review.images, this, imagesLinear)
            actionName.text = getString(R.string.editReview)
        }




        starImage1.setOnClickListener { setRatingAndDisplayStars(1) }
        starImage2.setOnClickListener { setRatingAndDisplayStars(2) }
        starImage3.setOnClickListener { setRatingAndDisplayStars(3) }
        starImage4.setOnClickListener { setRatingAndDisplayStars(4) }
        starImage5.setOnClickListener { setRatingAndDisplayStars(5) }

        uploadImagesButton.setOnClickListener {
            ImageHandler.pickImagesFromGallery(this@CreateReviewActivity)
        }

        submitReview.setOnClickListener {
            var finish: Boolean = false
            val newReviewText = reviewInput.text.toString()

            if (newReviewText.length > 3) {
                review?.let {
                    it.text = newReviewText
                    var updated: Boolean = false
                    if (receiveCreateOrEdit) {
                        updated = db.addReview(review)
                        if (updated) {
                            showSnackbar(getString(R.string.review_submitted))
                            finish = true
                        } else {
                            showSnackbar(getString(R.string.failed_submit_review))
                        }
                    } else {
                        updated = db.updateReview(review)
                        if (updated) {
                            showSnackbar(getString(R.string.review_updated))
                            finish = true
                        } else {
                            showSnackbar(getString(R.string.failed_update_review))
                        }
                    }



                }
            } else {
                (getString(R.string.review_length_error))
            }
            if (finish) {
                if (fromUserPage) {
                    val intent = Intent(this, Account::class.java)
                    ContextCompat.startActivity(this, intent, null)
                } else {
                    val intent = Intent(this, RestaurantViewActivity::class.java)
                    intent.putExtra("RESTAURANT_ID", restaurant.restaurantID)
                    ContextCompat.startActivity(this, intent, null)
                }

            }
        }
    }

    override fun onBackPressed() {
        if (fromUserPage) {
            val intent = Intent(this, Account::class.java)
            ContextCompat.startActivity(this, intent, null)
        } else {
            val intent = Intent(this, RestaurantViewActivity::class.java)
            intent.putExtra("RESTAURANT_ID", restaurant.restaurantID)
            ContextCompat.startActivity(this, intent, null)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ImageHandler.REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imagePaths = ImageHandler.handleImagePickerResult(this@CreateReviewActivity, data)
            review.images = review.images + ";" + imagePaths
        }
        Review.displayReviewImagesInLinearLayout(review.images, this, imagesLinear)
    }

    private fun setRatingAndDisplayStars(rating: Int) {
        review.rating = rating
        Review.displayStars(rating.toDouble(), starImage1, starImage2, starImage3, starImage4, starImage5)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
    }

    fun replaceReviewImages(paths: String) {
        review.images = paths
    }

    fun displayImages() {
        Review.displayReviewImagesInLinearLayout(review.images, this, imagesLinear)
    }
}
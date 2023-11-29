package com.example.cw

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity

class Review(var text: String, var reviewID: Int, var restaurant: Restaurant, var rating: Int, var user: User) {
    companion object {
        fun displayStars(score: Double, vararg stars: ImageView) {
            val roundedScore = score.toInt()

            for (i in stars.indices) {
                if (i < roundedScore) {
                    stars[i].setImageResource(R.drawable.star4review)
                    stars[i].isVisible = true
                } else {
                    stars[i].isVisible = false
                }
            }
        }

        fun displayReviewsInLinearLayout(reviewsList: ArrayList<Review>, context: Context, linear: LinearLayout) {
            val inflater = LayoutInflater.from(context)

            for (review in reviewsList) {
                val reviewLayout = inflater.inflate(R.layout.review, linear, false)

                val userPfp = reviewLayout.findViewById<ImageView>(R.id.userPfp)
                val userName = reviewLayout.findViewById<TextView>(R.id.userName)
                val reviewText = reviewLayout.findViewById<TextView>(R.id.reviewText)
                val moreOptions = reviewLayout.findViewById<ImageView>(R.id.moreOptions)
                val user = review.user

                userPfp.setImageBitmap(user.getUserPfpFromPath(context, R.drawable.reyzel_lezyer_photo_of_a_duck_soup_photorealistic_ad89f309_f9b3_4717_abda_a89ba176c68b))
                userName.text = review.user.username
                reviewText.text = review.text

                moreOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, moreOptions)

                    popupMenu.menuInflater.inflate(R.menu.review_options_menu, popupMenu.menu)

                    val currentUser = CurrentUser.currentUser
                    if (currentUser != null && currentUser.userID == review.user.userID) {
                        popupMenu.menu.findItem(R.id.edit_review_option).isVisible = true
                    } else {
                        popupMenu.menu.findItem(R.id.edit_review_option).isVisible = false
                    }

                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.report_option -> {
                                true
                            }
                            R.id.edit_review_option -> {
                                val intent = Intent(context, EditReviewActivity::class.java)
                                intent.putExtra("REVIEW_ID", review.reviewID)
                                startActivity(context, intent, null)
                                true
                            }
                            else -> false
                        }
                    }

                    popupMenu.show()
                }

                linear.addView(reviewLayout)
            }
        }

    }


}
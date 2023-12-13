package com.example.cw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import android.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class Review(var text: String, var reviewID: Int, var restaurant: Restaurant, var rating: Int, var user: User, var images: String) {
    companion object {
        lateinit var db: AppDatabase
        fun displayStars(score: Double, vararg stars: ImageView) {
            val roundedScore = score.toInt()

            for (i in stars.indices) {
                if (i < roundedScore) {
                    stars[i].setImageResource(R.drawable.star4review)
                    stars[i].isVisible = true
                    stars[i].setImageResource(R.drawable.star4review)
                } else {
                    stars[i].isVisible = true
                    stars[i].setImageResource(R.drawable.star4reviewblank)
                }
            }
        }

        fun displayReviewsInLinearLayout(reviewsList: ArrayList<Review>, context: Context, linear: LinearLayout, inUserPage: Boolean = false) {
            val inflater = LayoutInflater.from(context)

            for (review in reviewsList) {
                val reviewLayout = inflater.inflate(R.layout.review, linear, false)

                val userPfp = reviewLayout.findViewById<ImageView>(R.id.userPfp)
                val userName = reviewLayout.findViewById<TextView>(R.id.userName)
                val reviewText = reviewLayout.findViewById<TextView>(R.id.reviewText)
                val moreOptions = reviewLayout.findViewById<ImageView>(R.id.moreOptions)
                val s1 = reviewLayout.findViewById<ImageView>(R.id.starImage1)
                val s2 = reviewLayout.findViewById<ImageView>(R.id.starImage2)
                val s3 = reviewLayout.findViewById<ImageView>(R.id.starImage3)
                val s4 = reviewLayout.findViewById<ImageView>(R.id.starImage4)
                val s5 = reviewLayout.findViewById<ImageView>(R.id.starImage5)
                val constraint = reviewLayout.findViewById<ConstraintLayout>(R.id.reviewConstraint)
                val imagesLayout = reviewLayout.findViewById<LinearLayout>(R.id.imagesLinearReview)
                val user = review.user
                val mAuth = FirebaseAuth.getInstance()
                val currentUserFire = mAuth.currentUser
                db = AppDatabase(context)

                val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK



                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    constraint.setBackgroundColor(getColor(context, R.color.darkbackground))
                }

                if (currentUserFire != null) {
                    val currentUser = db.getUserById(currentUserFire.uid)
                    val blockedUsers = db.getUserBlocks(currentUser.userID)
                    if (user.userID in blockedUsers) {
                        review.text = "BLOCKED USER REVIEW"
                        user.username = "BLOCKED USER"
                        user.imagePath = ""
                        review.images = ""
                    }
                } else {
                    moreOptions.isVisible = false
                }

                if (inUserPage) {
                    userPfp.setImageBitmap(review.restaurant.getRestaurantImageFromPath(context, R.drawable.account_empty))
                    userName.text = review.restaurant.name
                } else {
                    userPfp.setImageBitmap(user.getUserPfpFromPath(context, R.drawable.account_empty))
                    userName.text = review.user.username
                }

                userPfp.setOnClickListener{
                    if (currentUserFire != null) {
                        val intent = Intent(context, Account::class.java)
                        intent.putExtra("SPECIFIC_USER", user.userID)
                        context.startActivity(intent)
                    }
                }

                reviewText.text = review.text

                Review.displayStars(review.rating.toDouble(), s1, s2, s3, s4, s5)
                Review.displayReviewImagesInLinearLayout(review.images, context, imagesLayout)
                if (review.images == "" || review.images == " ") {
                    imagesLayout.isVisible = false
                }


                moreOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, moreOptions)

                    popupMenu.menuInflater.inflate(R.menu.review_options_menu, popupMenu.menu)


                    if (currentUserFire != null) {
                        val currentUser = db.getUserById(currentUserFire.uid)
                        if (currentUser.userID == review.user.userID) {
                            popupMenu.menu.findItem(R.id.edit_review_option).isVisible = true
                        } else {
                            popupMenu.menu.findItem(R.id.edit_review_option).isVisible = false
                        }
                    } else {
                        popupMenu.menu.findItem(R.id.edit_review_option).isVisible = false
                    }

                    if(currentUserFire != null) {
                        popupMenu.menu.findItem(R.id.report_option).isVisible = true

                    } else {
                        popupMenu.menu.findItem(R.id.report_option).isVisible = false
                    }

                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.report_option -> {
                                if(currentUserFire != null) {
                                    val currentUser = db.getUserById(currentUserFire.uid)
                                    val report = Report(db.getFreeReportID(), review, currentUser)
                                    db.addReport(report)
                                    showSnackbar("Review Reported", context)
                                }

                                true
                            }
                            R.id.edit_review_option -> {
                                val intent = Intent(context, CreateReviewActivity::class.java)
                                intent.putExtra("REVIEW_ID", review.reviewID)
                                intent.putExtra("CREATE_OR_EDIT", false)
                                if (context is Account) {
                                    intent.putExtra("FROM_USER", true)
                                }
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


        fun displayReviewImagesInLinearLayout(imagePaths: String, context: Context, imagesLinear: LinearLayout) {
            val paths = imagePaths.split(";")
            imagesLinear.removeAllViews()
            for ((index, path) in paths.withIndex()) {
                if (path.isNotEmpty()) {
                    val imageView = ImageView(context)
                    val bitmap = BitmapFactory.decodeFile(path)

                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap)
                        imageView.adjustViewBounds = true
                        imageView.scaleType = ImageView.ScaleType.FIT_XY

                        val layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.setMargins(8, 0, 8, 0)
                        imageView.layoutParams = layoutParams

                        imageView.setOnClickListener {
                            val dialog = EnlargedImageDialogFragment()
                            val bundle = Bundle()
                            bundle.putString("imagePath", imagePaths)
                            bundle.putString("path", path)
                            dialog.arguments = bundle

                            if (context is AppCompatActivity) {
                                val fragmentManager = context.supportFragmentManager
                                dialog.show(fragmentManager, "enlarged_image_dialog")
                            }
                        }

                        imagesLinear.addView(imageView)
                    }
                }
            }
        }

        fun removeImagePath(imagePaths: String, pathToRemove: String): String {
            val paths = imagePaths.split(";")
            val updatedPaths = paths.toMutableList()
            updatedPaths.remove(pathToRemove)
            return updatedPaths.joinToString(";")

        }

        private fun showSnackbar(message: String, context: Context) {
            Snackbar.make((context as Activity).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }


    }




}
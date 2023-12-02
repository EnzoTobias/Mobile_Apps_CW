package com.example.cw

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.fragment.app.FragmentManager


class Review(var text: String, var reviewID: Int, var restaurant: Restaurant, var rating: Int, var user: User, var images: String) {
    companion object {
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

        fun displayReviewsInLinearLayout(reviewsList: ArrayList<Review>, context: Context, linear: LinearLayout) {
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
                val user = review.user

                userPfp.setImageBitmap(user.getUserPfpFromPath(context, R.drawable.reyzel_lezyer_photo_of_a_duck_soup_photorealistic_ad89f309_f9b3_4717_abda_a89ba176c68b))
                userName.text = review.user.username
                reviewText.text = review.text

                Review.displayStars(review.rating.toDouble(), s1, s2, s3, s4, s5)


                moreOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, moreOptions)

                    popupMenu.menuInflater.inflate(R.menu.review_options_menu, popupMenu.menu)

                    val currentUser = CurrentUser.currentUser
                    if (currentUser != null && currentUser.userID == review.user.userID) {
                        popupMenu.menu.findItem(R.id.edit_review_option).isVisible = true
                    } else {
                        popupMenu.menu.findItem(R.id.edit_review_option).isVisible = true
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
                            bundle.putString("imagePath", path)
                            bundle.putInt("index", index)
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

        fun removeImagePathAtIndex(imagePaths: String, indexToRemove: Int): String {
            val paths = imagePaths.split(";")
            if (indexToRemove >= 0 && indexToRemove < paths.size) {
                val updatedPaths = paths.toMutableList()
                updatedPaths.removeAt(indexToRemove)
                return updatedPaths.joinToString(";")
            }
            return imagePaths
        }

    }


}
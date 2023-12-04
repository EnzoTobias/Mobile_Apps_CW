package com.example.cw

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class Report(val reportID: Int, val review: Review, val user: User) {
    companion object {
        fun displayReviewsInLinearLayout(reportList: ArrayList<Report>, context: Context, linear: LinearLayout) {
            val inflater = LayoutInflater.from(context)
            linear.removeAllViews()
            for (report in reportList) {
                val reviewLayout = inflater.inflate(R.layout.review_report, linear, false)

                val userPfp = reviewLayout.findViewById<ImageView>(R.id.userPfp)
                val reportPfp = reviewLayout.findViewById<ImageView>(R.id.reportPfp)
                val reportText = reviewLayout.findViewById<TextView>(R.id.reportedText)
                val userName = reviewLayout.findViewById<TextView>(R.id.userName)
                val reviewText = reviewLayout.findViewById<TextView>(R.id.reviewText)
                val moreOptions = reviewLayout.findViewById<ImageView>(R.id.moreOptions)
                val s1 = reviewLayout.findViewById<ImageView>(R.id.starImage1)
                val s2 = reviewLayout.findViewById<ImageView>(R.id.starImage2)
                val s3 = reviewLayout.findViewById<ImageView>(R.id.starImage3)
                val s4 = reviewLayout.findViewById<ImageView>(R.id.starImage4)
                val s5 = reviewLayout.findViewById<ImageView>(R.id.starImage5)
                val imagesLayout = reviewLayout.findViewById<LinearLayout>(R.id.imagesLinearReview)
                val user = report.review.user
                val reportingUser = report.user

                userPfp.setImageBitmap(user.getUserPfpFromPath(context, R.drawable.reyzel_lezyer_photo_of_a_duck_soup_photorealistic_ad89f309_f9b3_4717_abda_a89ba176c68b))
                userName.text = report.review.user.username

                reportPfp.setImageBitmap(reportingUser.getUserPfpFromPath(context, R.drawable.reyzel_lezyer_photo_of_a_duck_soup_photorealistic_ad89f309_f9b3_4717_abda_a89ba176c68b))
                reportText.text = "Reported by: " + report.user.username




                reviewText.text = report.review.text

                Review.displayStars(report.review.rating.toDouble(), s1, s2, s3, s4, s5)
                Review.displayReviewImagesInLinearLayout(report.review.images, context, imagesLayout)
                if (report.review.images == "" || report.review.images == " ") {
                    imagesLayout.isVisible = false
                }

                moreOptions.setOnClickListener {
                    val popupMenu = PopupMenu(context, moreOptions)

                    popupMenu.menuInflater.inflate(R.menu.review_options_menu, popupMenu.menu)


                    popupMenu.menu.findItem(R.id.edit_review_option).title = "Delete Review"
                    popupMenu.menu.findItem(R.id.report_option).title = "Ignore Report"


                    val db = AppDatabase(context)
                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.report_option -> {
                                db.removeReport(report.review.reviewID)
                                showSnackbar("Report Ignored", context)
                                Report.displayReviewsInLinearLayout(db.getAllReports(), context, linear)
                                true
                            }
                            R.id.edit_review_option -> {
                                db.deleteReviewByID(report.review.reviewID)
                                db.removeReport(report.review.reviewID)
                                showSnackbar("Review Deleted", context)
                                Report.displayReviewsInLinearLayout(db.getAllReports(), context, linear)
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
        private fun showSnackbar(message: String, context: Context) {
            Snackbar.make((context as Activity).findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show()
        }


    }

}
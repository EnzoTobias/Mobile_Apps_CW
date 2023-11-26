package com.example.cw

import android.content.Context
import android.widget.ImageView
import androidx.core.view.isVisible

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

    }


}
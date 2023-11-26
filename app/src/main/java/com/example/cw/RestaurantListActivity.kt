package com.example.cw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RestaurantListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_list)

        val db = AppDatabase(this)
        db.populateDummyRestaurants()

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_restaurants) // Adjust as needed
        val itemDecoration = SpaceRecyclerView(spacingInPixels)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val restaurantList = db.getAllRestaurants()

        val adapter = RestaurantAdapter(restaurantList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(itemDecoration)
    }
}
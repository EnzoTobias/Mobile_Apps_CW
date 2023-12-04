package com.example.cw

import android.app.Activity
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RestaurantListActivity : BasicActivity() {

    private lateinit var placesClient: PlacesClient
    private val startAutocomplete =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {

                    val db = AppDatabase(this)
                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val placeID = place.id
                    val placeDetailsUrl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=$placeID&fields=photos&key=AIzaSyB0BVXaj3Ib7Hc4RHetfbAtuLsPOtzY-eQ"

                    var photoUrl: String = ""

                    val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.GET, placeDetailsUrl, null,
                        { response ->
                            val photosArray = response.getJSONObject("result").getJSONArray("photos")
                            if (photosArray.length() > 0) {
                                val photoReference = photosArray.getJSONObject(0).getString("photo_reference")
                                val maxPhotoWidth = 400
                                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=$maxPhotoWidth&photoreference=$photoReference&key=AIzaSyB0BVXaj3Ib7Hc4RHetfbAtuLsPOtzY-eQ"
                                val placeName = place.name
                                val placePhone = place.phoneNumber
                                ImageHandler.saveImageToInternalStorageFromUrl(this, photoUrl) { imagePath ->
                                    if (imagePath != null) {
                                        val placeDesc = "Address: " + place.address + " \nPhone Number " + placePhone
                                        var newRes = Restaurant(placeName!!, db.getFreeRestaurantID(),
                                            imagePath, placeDesc)
                                        val resList = db.getAllRestaurants()
                                        var resExists: Boolean = false
                                        for (restaurant in resList) {
                                            if (restaurant.description == placeDesc && restaurant.name == placeName) {
                                                resExists = true
                                                newRes = restaurant
                                            }
                                        }
                                        if (!resExists) {
                                            db.addRestaurant(newRes)
                                        }
                                        val intentToGo = Intent(this, RestaurantViewActivity::class.java)
                                        intentToGo.putExtra("RESTAURANT_ID", newRes.restaurantID)
                                        this.startActivity(intentToGo)
                                    } else {
                                        println("Failed to save image.")
                                    }
                                }
                            }
                        },
                        { error ->
                            // Handle error
                        })
                    val queue = Volley.newRequestQueue(this)
                    queue.add(jsonObjectRequest)


                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "User canceled autocomplete")
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyB0BVXaj3Ib7Hc4RHetfbAtuLsPOtzY-eQ")
        }

        placesClient = Places.createClient(this)

        val db = AppDatabase(this)

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing_between_restaurants)
        val itemDecoration = SpaceRecyclerView(spacingInPixels)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val restaurantList = db.getAllRestaurants()
        val autocompleteRes = findViewById<Button>(R.id.newResButton)

        val adapter = RestaurantAdapter(restaurantList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(itemDecoration)

        autocompleteRes.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.PHOTO_METADATAS, Place.Field.ICON_URL,Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(this)
            startAutocomplete.launch(intent)
        }
        //db.populateDummyRestaurants()
    }
    override fun onBackPressed() {
        this.finish()
    }

    override fun getLayoutID(): Int {
        return R.layout.activity_restaurant_list
    }
}
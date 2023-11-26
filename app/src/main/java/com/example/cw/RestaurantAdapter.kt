package com.example.cw
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class RestaurantAdapter(private val restaurantList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDesc: TextView = itemView.findViewById(R.id.textViewDescription)
        val starImage1: ImageView = itemView.findViewById(R.id.starImage1)
        val starImage2: ImageView = itemView.findViewById(R.id.starImage2)
        val starImage3: ImageView = itemView.findViewById(R.id.starImage3)
        val starImage4: ImageView = itemView.findViewById(R.id.starImage4)
        val starImage5: ImageView = itemView.findViewById(R.id.starImage5)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.imageView.setImageBitmap(restaurant.getRestaurantImageFromPath(holder.itemView.context, R.drawable.reyzel_lezyer_photo_of_a_burger_photorealistic_23f4b9f9_7c15_447b_b58c_41631ebe89c2))
        holder.textViewName.text = restaurant.name
        holder.textViewDesc.maxLines = 5
        holder.textViewDesc.ellipsize = TextUtils.TruncateAt.END
        holder.textViewDesc.text = restaurant.description
        val db = AppDatabase(holder.itemView.context)
        val restaurantScore = db.getRestaurantScore(restaurant)
        Review.displayStars(restaurantScore, holder.starImage1, holder.starImage2, holder.starImage3, holder.starImage4, holder.starImage5)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RestaurantViewActivity::class.java)
            intent.putExtra("RESTAURANT_ID", restaurant.restaurantID)
            context.startActivity(intent)
        }

        }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

}


package com.example.gourmetsearchapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

class RestaurantAdapter(private var shopList: List<Shop>) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivRestaurant: ImageView = view.findViewById(R.id.ivRestaurant)
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvCatchphrase: TextView = view.findViewById(R.id.tvCatchphrase)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvAccess: TextView = view.findViewById(R.id.tvAccess)
        val tvDistance: TextView = view.findViewById(R.id.tvDistance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop = shopList[position]

        holder.tvRestaurantName.text = shop.name
        holder.tvCatchphrase.text = shop.catchCopy
        holder.tvAddress.text = shop.address
        holder.tvAccess.text = shop.access
        holder.tvDistance.text = "現在地から${shop.distance}"
        holder.ivRestaurant.load(shop.imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.stat_notify_error)
        }
    }

    override fun getItemCount(): Int = shopList.size

    fun updateData(newList: List<Shop>) {
        shopList = newList
        notifyDataSetChanged()
    }
}
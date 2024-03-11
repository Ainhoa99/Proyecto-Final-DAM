package com.txurdinaga.proyectofinaldam.ui


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.txurdinaga.proyectofinaldam.R

class ImageAdapter(private val context: Context, private val images: List<CardData>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView
        init {
            image = view.findViewById(R.id.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentCard = images[position]
        Glide.with(context)
            .load(currentCard.url)
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        // Retornamos un valor grande para que parezca que la lista es infinita
        return images.size
    }
}


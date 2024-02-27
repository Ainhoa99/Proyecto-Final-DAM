package com.txurdinaga.proyectofinaldam.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.txurdinaga.proyectofinaldam.R

class ImageAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResId = images[position % images.size]
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int {
        // Retornamos un valor grande para que parezca que la lista es infinita
        return Int.MAX_VALUE
    }
}


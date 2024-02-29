package com.txurdinaga.proyectofinaldam.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.card.MaterialCardView
import com.txurdinaga.proyectofinaldam.R
import kotlin.coroutines.coroutineContext


class CardAdapter(private val dataSet: List<CardData>, private val clickListener: ICardClickListener):
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(view: View, clickListener: ICardClickListener) : RecyclerView.ViewHolder(view) {
        val materialCardView: MaterialCardView
        val image: ImageView
        val name: TextView
        val ocupacion: TextView

        var cardData: CardData? = null
        init {
            materialCardView = view.findViewById(R.id.materialCardView)
            image = view.findViewById(R.id.image)
            name = view.findViewById(R.id.name)
            ocupacion = view.findViewById(R.id.rol)

            // Click listener for the whole card
            materialCardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedCardData = cardData
                    if (clickedCardData != null) {
                        clickListener.onCardClick(position, clickedCardData)
                    }
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.material_card, parent, false)

        return ViewHolder(view, clickListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentCard = dataSet[position]
        if (currentCard.title!=null){
            val resourceId = holder.itemView.context.resources.getIdentifier(currentCard.title, "drawable", holder.itemView.context.packageName)
            holder.image.setImageResource(resourceId)
        } else{
            holder.image.setImageResource(R.drawable.avatar)
        }

        if(currentCard.nombre!=null){
            holder.name.text = currentCard.nombre.toString()
            holder.name.visibility = View.VISIBLE
        }

        if(currentCard.ocupacion!=null && currentCard.ocupacion!="Jugador"){
            holder.ocupacion.text = currentCard.ocupacion.toString()
            holder.ocupacion.visibility = View.VISIBLE
        }


        // Set CardData in the ViewHolder
        holder.cardData = currentCard
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}


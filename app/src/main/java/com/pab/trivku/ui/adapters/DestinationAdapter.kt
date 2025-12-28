package com.pab.trivku.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.pab.trivku.DetailKonten
import com.pab.trivku.R
import com.pab.trivku.data.models.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.pab.trivku.data.local.FavoriteDao

class DestinationAdapter(
    private var destinations: List<Destination>,
    private val onFavoriteClick: ((Destination, Int) -> Unit)?,
    private val useSecondaryCard: Boolean = false,
    private val favoriteDao: FavoriteDao,
    private val userId: Int,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<DestinationAdapter.DestinationViewHolder>() {

    class DestinationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.destination_title)
        val location: TextView = view.findViewById(R.id.destination_location)
        val image: ShapeableImageView = view.findViewById(R.id.destination_image)
        val fav: ImageView = view.findViewById(R.id.ivFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val layoutRes =
            if (useSecondaryCard) R.layout.item_destination_card2 else R.layout.item_destination_card
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = destinations[position]

        holder.title.text = destination.name
        holder.location.text = destination.location
        holder.image.setImageResource(destination.imageResource)

// Tampilkan status yang ada di memori dulu (Instan)
        holder.fav.setImageResource(if (destination.isFavorite) R.drawable.heart_filled else R.drawable.heart)

// Cek database untuk memastikan data sinkron
        scope.launch {
            val isFavInDb = favoriteDao.isFavorite(userId, destination.id)
            withContext(Dispatchers.Main) {
                // Hanya update UI jika data di DB berbeda dengan yang di memori
                if (destination.isFavorite != isFavInDb) {
                    destination.isFavorite = isFavInDb
                    holder.fav.setImageResource(if (isFavInDb) R.drawable.heart_filled else R.drawable.heart)
                }
            }
        }

        // === FAVORITE CLICK ===
        holder.fav.setOnClickListener {
            onFavoriteClick?.invoke(destination, position)
        }

        // === DETAIL CLICK ===
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailKonten::class.java)
            intent.putExtra("EXTRA_DESTINATION", destination)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = destinations.size

    fun updateList(newList: List<Destination>) {
        val diffCallback = object : androidx.recyclerview.widget.DiffUtil.Callback() {
            override fun getOldListSize() = destinations.size
            override fun getNewListSize() = newList.size

            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
                // Membandingkan ID item
                return destinations[oldPos].id == newList[newPos].id
            }

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
                // Membandingkan isi konten item
                return destinations[oldPos] == newList[newPos]
            }
        }

        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)

        // Update data list
        this.destinations = newList

        // Trigger perubahan hanya
        diffResult.dispatchUpdatesTo(this)
    }
}

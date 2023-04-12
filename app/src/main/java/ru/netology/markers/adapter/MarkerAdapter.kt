package ru.netology.markers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.markers.dto.Marker
import android.widget.PopupMenu
import ru.netology.markers.R
import ru.netology.markers.databinding.MarkerBinding

interface OnInteractionListener {
    fun onEdit(marker: Marker) {}
    fun onRemove(marker: Marker) {}
    fun onPlace(marker: Marker) {}
}

class MarkerAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Marker, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = (MarkerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: MarkerBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(marker: Marker) {
        binding.apply {
            longitude.text = marker.Point.longitude.toString().take(10)
            latitude.text = marker.Point.latitude.toString().take(10)
            nameMarker.text = marker.name

            root.setOnClickListener {
                onInteractionListener.onPlace(marker)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(marker)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(marker)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Marker>() {
    override fun areItemsTheSame(oldItem: Marker, newItem: Marker): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Marker, newItem: Marker): Boolean {
        return oldItem == newItem
    }
}
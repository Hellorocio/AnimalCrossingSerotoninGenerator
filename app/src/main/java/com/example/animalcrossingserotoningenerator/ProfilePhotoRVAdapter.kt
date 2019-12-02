package com.example.animalcrossingserotoningenerator

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ProfilePhotoRVAdapter(private val viewModel: ProfilePhotoViewModel)
    : RecyclerView.Adapter<ProfilePhotoRVAdapter.VH>() {

    // Adapter does not have its own copy of list, it just observes
    private var pictures = listOf<String>()


    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        var theImageView = itemView.findViewById<ImageView>(R.id.iv)
        init {
            theImageView.setOnClickListener {
                Log.d("XXX", "clicked photo num $adapterPosition")
                ProfileActivity.selectProfilePhoto(pictures[adapterPosition])
            }
        }
        fun bind(item: String?) {
            if(item == null) return
            theImageView.setImageResource(itemView.context.resources.getIdentifier(item, "drawable", itemView.context.packageName))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_box, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(pictures[holder.adapterPosition])
    }

    fun submitList(items: List<String>) {
        pictures = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = pictures.size
}
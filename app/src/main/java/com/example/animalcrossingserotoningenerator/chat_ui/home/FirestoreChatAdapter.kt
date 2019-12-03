package com.example.animalcrossingserotoningenerator

import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.firestore.ChatRow
import java.util.UUID.randomUUID


class FirestoreChatAdapter(private var viewModel: ChatViewModel)
    : ListAdapter<ChatRow, FirestoreChatAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<ChatRow>() {
        override fun areItemsTheSame(oldItem: ChatRow, newItem: ChatRow): Boolean {
            return oldItem.timeStamp == newItem.timeStamp
        }

        override fun areContentsTheSame(oldItem: ChatRow, newItem: ChatRow): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.message == newItem.message
        }
    }

    private val dateFormat: DateFormat =
        SimpleDateFormat("hh:mm:ss MM-dd-yyyy")
    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var userTV = itemView.findViewById<TextView>(R.id.chatUserTV)
        private var timeTV = itemView.findViewById<TextView>(R.id.chatTimeTV)
        private var textTV = itemView.findViewById<TextView>(R.id.chatTextTV)
        init {
            userTV.setOnClickListener {
                val intent = Intent(itemView.context, ProfileActivity::class.java)
                val myExtras = Bundle()
                myExtras.putBoolean("mine", MainActivity.auth.getEmail() == getItem(adapterPosition).email)
                myExtras.putString("email", getItem(adapterPosition).email)
                intent.putExtras(myExtras)
                itemView.context.startActivity(intent)
            }
        }
        fun bind(item: ChatRow?) {
            if (item == null) return
            userTV.text = item.name
            textTV.text = item.message
            Log.d("XXX", "hi, this is my picture id thing: ${item.pictureUUID}")
            if(item.pictureUUID.toString() != "") {
                Log.d("XXX", "hi, this is my picture id thing: ${item.pictureUUID}")
                viewModel.downloadJpg(item.pictureUUID.toString(), textTV)
            }


            if (item.timeStamp != null) {
                timeTV.text = dateFormat.format(item.timeStamp.toDate())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_chat, parent, false)
        //Log.d(MainActivity.TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //Log.d(MainActivity.TAG, "Bind pos $position")
        holder.bind(getItem(holder.adapterPosition))
    }
}

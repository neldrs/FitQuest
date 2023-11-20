package com.example.fitquest.ui.nutrition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.ui.nutrition.RowEntry

class EntryAdapter : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    val entries: MutableList<RowEntry> = mutableListOf()

    fun addEntry(entry: RowEntry) {
        entries.add(entry)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_entry_item, parent, false)
        return EntryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        holder.bind(entry)
    }

    override fun getItemCount(): Int = entries.size

    class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(entry: RowEntry) {
            // Bind data to your entry layout views
            // You can customize this based on your layout structure
        }
    }
}
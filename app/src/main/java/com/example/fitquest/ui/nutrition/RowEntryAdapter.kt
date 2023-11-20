package com.example.fitquest.ui.nutrition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.ui.nutrition.NutritionViewModel

interface OnItemClickListener {
    fun onDeleteClick(position: Int)
}

class RowEntryAdapter(
    var entries: MutableList<RowEntry>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RowEntryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textString: TextView = itemView.findViewById(R.id.textString)
        val textNumber: TextView = itemView.findViewById(R.id.textNumber)

        init {
            itemView.findViewById<ImageButton>(R.id.deleteButton).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_entry_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rowEntry = entries[position]
        holder.textString.text = rowEntry.textString
        holder.textNumber.text = rowEntry.textNumber.toString()
    }

    override fun getItemCount(): Int = entries.size
}

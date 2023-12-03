package com.example.fitquest.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R

class RVRunRecordAdapter(private var runList: List<RunRecord>) : RecyclerView.Adapter<RVRunRecordAdapter.RunViewHolder>() {

    class RunViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewDistance: TextView = view.findViewById(R.id.textViewDistance)
        val textViewTime: TextView = view.findViewById(R.id.textViewTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.run_record_item, parent, false)
        return RunViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val currentRun = runList[position]
        holder.textViewDate.text = currentRun.date
        holder.textViewDistance.text = currentRun.distance.toString() + " miles"
        holder.textViewTime.text = currentRun.time
    }
    fun updateData(newRunList: List<RunRecord>) {
        this.runList = newRunList
        notifyDataSetChanged()
    }

    override fun getItemCount() = runList.size
}
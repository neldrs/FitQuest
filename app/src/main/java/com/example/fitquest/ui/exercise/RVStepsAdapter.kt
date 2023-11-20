package com.example.fitquest.ui.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R

class RVStepsAdapter(private var stepEntries: List<StepEntry>) : RecyclerView.Adapter<RVStepsAdapter.StepViewHolder>() {

    class StepViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_text_view)
        val stepCountTextView: TextView = view.findViewById(R.id.step_count_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.steps_item, parent, false)
        return StepViewHolder(view)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        val stepEntry = stepEntries[position]
        holder.dateTextView.text = stepEntry.date
        holder.stepCountTextView.text = "${stepEntry.stepCount} steps"
    }

    override fun getItemCount() = stepEntries.size

    fun updateData(newStepEntries: List<StepEntry>) {
        stepEntries = newStepEntries
        notifyDataSetChanged()
    }
}

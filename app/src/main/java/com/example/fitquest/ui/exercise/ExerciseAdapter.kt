package com.example.fitquest.ui.exercise


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitquest.R
import com.example.fitquest.ui.nutrition.OnItemClickListener
import com.example.fitquest.ui.nutrition.RowEntry
import com.example.fitquest.ui.nutrition.RowEntryAdapter
import kotlin.math.E

interface OnItemClickListener {
    fun onDeleteClick(position: Int)
}

class ExerciseAdapter(
    var entries: MutableList<Exercise>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val exerciseText: TextView = itemView.findViewById(R.id.exerciseText)
        val setText: TextView = itemView.findViewById(R.id.setText)
        val repText: TextView = itemView.findViewById(R.id.repText)
        val weightText: TextView = itemView.findViewById(R.id.weightText)


        init {
            itemView.findViewById<AppCompatButton>(R.id.deleteButton).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weight_training_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseAdapter.ViewHolder, position: Int) {
        val exercise = entries[position]
        holder.exerciseText.text = exercise.exerciseType
        holder.setText.text = exercise.sets.toString()
        holder.repText.text = exercise.reps.toString()
        holder.weightText.text = exercise.weight.toString()

    }

    override fun getItemCount(): Int = entries.size

}

/**
class ExerciseAdapter : ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_weight_training, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val currentExercise = getItem(position)
        holder.bind(currentExercise)
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseTypeTextView: TextView = itemView.findViewById(R.id.exerciseType)
        private val repsTextView: TextView = itemView.findViewById(R.id.reps)
        private val setsTextView: TextView = itemView.findViewById(R.id.sets)
        private val weightTextView: TextView = itemView.findViewById(R.id.weight)

        fun bind(exercise: Exercise) {
            exerciseTypeTextView.text = exercise.exerciseType
            repsTextView.text = exercise.reps
            setsTextView.text = exercise.sets
            weightTextView.text = exercise.weight
        }
    }

    // DiffUtil for efficient updates in the list
    class ExerciseDiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }
}
        **/
package com.thanhtuan.myapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thanhtuan.myapp.models.TaskStatus
import com.thanhtuan.myapp.databinding.ItemTaskBinding
import com.thanhtuan.myapp.models.Task

class TaskAdapter(
    private val onOptionsClick: (Task) -> Unit,
    private val onCompletedClick: (Task) -> Unit,
    private val onNotCompletedClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskTime.text = task.time.toString() // TODO: Format time properly
                
                // Handle task status
                when (task.status) {
                    TaskStatus.COMPLETED -> {
                        buttonCompleted.isEnabled = false
                        buttonNotCompleted.isEnabled = true
                    }
                    TaskStatus.FAILED -> {
                        buttonCompleted.isEnabled = true
                        buttonNotCompleted.isEnabled = false
                    }
                    else -> {
                        buttonCompleted.isEnabled = true
                        buttonNotCompleted.isEnabled = true
                    }
                }

                // Handle task photo
                task.photoUrl?.let { url ->
                    taskPhoto.visibility = ViewGroup.VISIBLE
                    // TODO: Load image using your preferred image loading library
                } ?: run {
                    taskPhoto.visibility = ViewGroup.GONE
                }

                // Handle task evaluation
                task.evaluation?.let { evaluation ->
                    taskEvaluation.visibility = ViewGroup.VISIBLE
                    taskEvaluation.text = evaluation
                } ?: run {
                    taskEvaluation.visibility = ViewGroup.GONE
                }

                // Set click listeners
                buttonOptions.setOnClickListener {
                    onOptionsClick(task)
                }
                
                buttonCompleted.setOnClickListener {
                    onCompletedClick(task)
                }
                
                buttonNotCompleted.setOnClickListener {
                    onNotCompletedClick(task)
                }
            }
        }
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
} 
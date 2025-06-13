package com.thanhtuan.myapp.repositories.adapters

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thanhtuan.myapp.databinding.ItemTaskBinding
import com.thanhtuan.myapp.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(
    private val onCompleted: ((Task) -> Unit)? = null,
    private val onNotCompleted: ((Task) -> Unit)? = null,
    private val onPhotoClick: ((Task) -> Unit)? = null,
    private val onOptionsClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()), Parcelable {

    constructor() : this(
        TODO("onCompleted"),
        TODO("onNotCompleted"),
        TODO("onPhotoClick"),
        TODO("onOptionsClick")
    ) {
    }

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

        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(task: Task) {
            binding.apply {
                tvTaskTitle.text = task.title
                tvTaskTime.text = "Thời gian: ${task.time} phút"
                taskStatus.text = task.status.toString()

                // Show evaluation buttons only if callbacks are provided (child view)
                if (onCompleted != null && onNotCompleted != null) {
                    buttonCompleted.visibility = ViewGroup.VISIBLE
                    buttonNotCompleted.visibility = ViewGroup.VISIBLE
                    
                    buttonCompleted.setOnClickListener { onCompleted?.let { it1 -> it1(task) } }
                    buttonNotCompleted.setOnClickListener { onNotCompleted?.let { it1 -> it1(task) } }
                } else {
                    buttonCompleted.visibility = ViewGroup.GONE
                    buttonNotCompleted.visibility = ViewGroup.GONE
                }

                // Show evaluation text if available
                task.evaluation?.let { evaluation ->
                    taskEvaluation.visibility = ViewGroup.VISIBLE
                    taskEvaluation.text = evaluation
                } ?: run {
                    taskEvaluation.visibility = ViewGroup.GONE
                }

                // Load and display task photo if available
                task.photoUrl?.let { photoUrl ->
                    taskPhoto.visibility = ViewGroup.VISIBLE
                    Glide.with(taskPhoto)
                        .load(photoUrl)
                        .centerCrop()
                        .into(taskPhoto)

                    // Set click listener for photo
                    taskPhoto.setOnClickListener {
                        onPhotoClick?.invoke(task)
                    }
                } ?: run {
                    taskPhoto.visibility = ViewGroup.GONE
                }

//                btnOptions.setOnClickListener {
//                    onOptionsClick(task)
//                }
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskAdapter> {
        override fun createFromParcel(parcel: Parcel): TaskAdapter {
            return TaskAdapter()
        }

        override fun newArray(size: Int): Array<TaskAdapter?> {
            return arrayOfNulls(size)
        }
    }
} 
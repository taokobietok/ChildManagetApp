package com.thanhtuan.myapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.thanhtuan.myapp.databinding.ActivityTaskEditBinding
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.TaskStatus
import com.thanhtuan.myapp.viewmodels.TaskViewModel
import com.thanhtuan.myapp.viewmodels.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class ActivityTaskEdit : AppCompatActivity() {
    private lateinit var binding: ActivityTaskEditBinding
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.taskState.observe(this) { state ->
            when (state) {
                is TaskViewModel.TaskState.Loading -> showLoading(true)
                is TaskViewModel.TaskState.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is TaskViewModel.TaskState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, viewModel.error.value, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.okButton.setOnClickListener {
            val taskName = binding.taskNameInput.text.toString()
            val timeStr = binding.timeInput.text.toString()

            if (taskName.isBlank()) {
                binding.taskNameInput.error = "Please enter task name"
                return@setOnClickListener
            }

            val time = timeStr.toLongOrNull()
            if (time == null || time <= 0) {
                binding.timeInput.error = "Please enter valid time"
                return@setOnClickListener
            }

            val childUid = intent.getStringExtra("child_uid")
            if (childUid == null) {
                Toast.makeText(this, "Child UID not found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val task = Task(
                taskId = UUID.randomUUID().toString(),
                title = taskName,
                time = time,
                assignedTo = childUid,
                createdBy = currentUser.uid,
                status = TaskStatus.PENDING
            )

            viewModel.createTask(task)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.okButton.isEnabled = !show
    }
} 
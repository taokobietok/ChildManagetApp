package com.thanhtuan.myapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.repositories.adapters.TaskAdapter
import com.thanhtuan.myapp.databinding.ActivityTaskListBinding
import com.thanhtuan.myapp.viewmodels.TaskViewModel

import com.google.firebase.auth.FirebaseAuth

class ActivityTaskList : AppCompatActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private val viewModel: TaskViewModel by viewModels()


    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        loadTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter()
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(this@ActivityTaskList)
            adapter = taskAdapter
        }
    }

    private fun setupObservers() {
        viewModel.taskState.observe(this) { status ->
            when (status) {
                is TaskViewModel.TaskState.Loading -> showLoading(true)
                is TaskViewModel.TaskState.Success -> showLoading(false)
                is TaskViewModel.TaskState.Error -> {
                    showLoading(false)
                    Toast.makeText(this, viewModel.error.value, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.tasks.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun loadTasks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val isParent = intent.getBooleanExtra("is_parent", false)
        if (isParent) {
            viewModel.loadParentTasks(currentUser.uid)
        } else {
            viewModel.loadChildTasks(currentUser.uid)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
} 
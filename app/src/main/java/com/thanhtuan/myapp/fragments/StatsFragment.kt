package com.thanhtuan.myapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.adapters.TaskAdapter
import com.thanhtuan.myapp.databinding.FragmentStatsBinding
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.TaskStatus
import com.thanhtuan.myapp.viewmodels.TaskViewModel
import com.thanhtuan.myapp.viewmodels.TaskViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class StatsFragment : Fragment() {
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(requireContext()) }
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        loadTasks()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onOptionsClick = { task ->
                showTaskOptions(task)
            },
            onCompletedClick = { task ->
                viewModel.updateTaskStatus(task.taskId, TaskStatus.COMPLETED)
            },
            onNotCompletedClick = { task ->
                viewModel.updateTaskStatus(task.taskId, TaskStatus.FAILED)
            }
        )
        binding.recyclerViewPendingSchedules.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun setupObservers() {
        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskViewModel.TaskState.Loading -> showLoading(true)
                is TaskViewModel.TaskState.Success -> showLoading(false)
                is TaskViewModel.TaskState.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), viewModel.error.value, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }
    }

    private fun loadTasks() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.loadTasks(currentUser.uid)
    }

    private fun showTaskOptions(task: Task) {
        // TODO: Implement task options dialog/menu
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
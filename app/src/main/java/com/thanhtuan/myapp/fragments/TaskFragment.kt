package com.thanhtuan.myapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.R
import com.thanhtuan.myapp.adapters.TaskAdapter
import com.thanhtuan.myapp.databinding.FragmentTaskBinding
import com.thanhtuan.myapp.models.Task
import com.thanhtuan.myapp.models.TaskStatus
import com.thanhtuan.myapp.viewmodels.TaskViewModel
import com.thanhtuan.myapp.viewmodels.TaskViewModelFactory

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TaskViewModel by viewModels { TaskViewModelFactory(requireContext()) }
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
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
        binding.recyclerViewTasks.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }

    private fun setupObservers() {
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.submitList(tasks)
        }

        viewModel.taskState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TaskViewModel.TaskState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is TaskViewModel.TaskState.Success -> {
                    binding.progressBar.visibility = View.GONE
                }
                is TaskViewModel.TaskState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, viewModel.error.value, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTask.setOnClickListener {
            findNavController().navigate(R.id.action_taskFragment_to_taskEditFragment)
        }
    }

    private fun loadTasks() {
        // TODO: Get current user ID from your auth system
        val userId = "current_user_id"
        viewModel.loadTasks(userId)
    }

    private fun showTaskOptions(task: Task) {
        // TODO: Implement task options dialog/menu
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
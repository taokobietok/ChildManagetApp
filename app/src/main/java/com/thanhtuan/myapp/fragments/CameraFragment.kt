package com.thanhtuan.myapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.thanhtuan.myapp.databinding.FragmentCameraBinding
import com.thanhtuan.myapp.viewmodels.TaskViewModel

class CameraFragment : Fragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: TaskViewModel by viewModels()
    private var currentPhotoUri: Uri? = null
    private var currentTaskId: String? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                binding.imagePreview.setImageURI(uri)
                binding.btnUpload.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupObservers()
        
        // Get taskId from arguments
        currentTaskId = arguments?.getString("taskId")
        if (currentTaskId == null) {
            Toast.makeText(requireContext(), "Task ID not found", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.cameraButton.setOnClickListener {
            checkCameraPermissionAndTakePicture()
        }

        binding.btnUpload.setOnClickListener {
            currentPhotoUri?.let { uri ->
                currentTaskId?.let { taskId ->
                    viewModel.uploadPhoto(taskId, uri)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.taskState.observe(viewLifecycleOwner) { status ->
            when (status) {
                is TaskViewModel.TaskState.Loading -> showLoading(true)
                is TaskViewModel.TaskState.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
                is TaskViewModel.TaskState.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), viewModel.error.value, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkCameraPermissionAndTakePicture() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePicture() {
        val photoFile = createImageFile()
        currentPhotoUri = photoFile
        takePictureLauncher.launch(photoFile)
    }

    private fun createImageFile(): Uri {
        val timestamp = System.currentTimeMillis()
        val imageFileName = "JPEG_${timestamp}_"
        val storageDir = requireContext().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val imageFile = java.io.File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        return Uri.fromFile(imageFile)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.cameraButton.isEnabled = !show
        binding.btnUpload.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
package com.thanhtuan.myapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.thanhtuan.myapp.R
import com.thanhtuan.myapp.databinding.FragmentProfileBinding
import com.thanhtuan.myapp.viewmodels.UserViewModel
import com.thanhtuan.myapp.viewmodels.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModels { UserViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        loadUserData()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.apply {
                    tvName.text = it.name
                    tvEmail.text = it.email
                    tvRole.text = if (it.isParent) "Parent" else "Child"
                    tvAge.apply {
                        visibility = if (!it.isParent && it.age != null) View.VISIBLE else View.GONE
                        text = "Age: ${it.age}"
                    }
                }
            }
        }

        viewModel.userStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is UserViewModel.UserStatus.Loading -> showLoading(true)
                is UserViewModel.UserStatus.Success -> {
                    showLoading(false)
                    if (viewModel.user.value == null) {
                        // User has signed out, navigate to login
                        findNavController().navigate(R.id.loginFragment)
                    }
                }
                is UserViewModel.UserStatus.Error -> {
                    showLoading(false)
                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
        }
    }

    private fun loadUserData() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            viewModel.loadUser(user.uid)
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignOut.isEnabled = !show
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
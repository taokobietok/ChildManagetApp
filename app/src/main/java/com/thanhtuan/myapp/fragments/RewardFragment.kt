package com.thanhtuan.myapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.thanhtuan.myapp.repositories.adapters.RewardAdapter
import com.thanhtuan.myapp.databinding.FragmentRewardBinding
import com.thanhtuan.myapp.models.User
import com.thanhtuan.myapp.viewmodels.RewardViewModel
import com.thanhtuan.myapp.viewmodels.RewardViewModelFactory

class RewardFragment : Fragment() {
    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: RewardViewModel by viewModels { RewardViewModelFactory() }
    private lateinit var rewardAdapter: RewardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        rewardAdapter = RewardAdapter { reward ->
            viewModel.redeemReward(reward)
        }
        binding.recyclerViewRewards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rewardAdapter
        }
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvCoins.text = "Your coins: ${it.coins}"
            }
        }

        viewModel.rewards.observe(viewLifecycleOwner) { rewards ->
            rewardAdapter.submitList(rewards)
        }

        viewModel.rewardStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is RewardViewModel.RewardStatus.Loading -> showLoading(true)
                is RewardViewModel.RewardStatus.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Reward redeemed successfully!", Toast.LENGTH_SHORT).show()
                }
                is RewardViewModel.RewardStatus.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setUser(user: User) {
        viewModel.loadUser(user)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 
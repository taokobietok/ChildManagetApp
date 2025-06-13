package com.thanhtuan.myapp.repositories.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.thanhtuan.myapp.databinding.ItemRewardBinding
import com.thanhtuan.myapp.models.Reward

class RewardAdapter(
    private val onRedeemClick: (Reward) -> Unit
) : ListAdapter<Reward, RewardAdapter.RewardViewHolder>(RewardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val binding = ItemRewardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RewardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RewardViewHolder(
        private val binding: ItemRewardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reward: Reward) {
            binding.apply {
                tvRewardName.text = reward.name
                tvCoinCost.text = "${reward.coinCost} coins"
                btnRedeem.setOnClickListener {
                    onRedeemClick(reward)
                }
            }
        }
    }

    private class RewardDiffCallback : DiffUtil.ItemCallback<Reward>() {
        override fun areItemsTheSame(oldItem: Reward, newItem: Reward): Boolean {
            return oldItem.rewardId == newItem.rewardId
        }

        override fun areContentsTheSame(oldItem: Reward, newItem: Reward): Boolean {
            return oldItem == newItem
        }
    }
} 
package com.thanhtuan.myapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thanhtuan.myapp.models.Reward
import com.thanhtuan.myapp.models.User
import com.thanhtuan.myapp.repositories.RewardRepository
import kotlinx.coroutines.launch

class RewardViewModel(private val repository: RewardRepository) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _rewards = MutableLiveData<List<Reward>>()
    val rewards: LiveData<List<Reward>> = _rewards

    private val _rewardStatus = MutableLiveData<RewardStatus>()
    val rewardStatus: LiveData<RewardStatus> = _rewardStatus

    init {
        _rewards.value = repository.getAvailableRewards()
    }

    fun loadUser(user: User) {
        _user.value = user
    }

    fun redeemReward(reward: Reward) {
        val currentUser = _user.value
        if (currentUser == null) {
            _rewardStatus.value = RewardStatus.Error("User not found")
            return
        }

        viewModelScope.launch {
            _rewardStatus.value = RewardStatus.Loading
            repository.redeemReward(currentUser.uid, reward).fold(
                onSuccess = {
                    // Update user's coins locally
                    _user.value = currentUser.copy(coins = currentUser.coins - reward.coinCost)
                    _rewardStatus.value = RewardStatus.Success
                },
                onFailure = { error ->
                    _rewardStatus.value = RewardStatus.Error(error.message ?: "Failed to redeem reward")
                }
            )
        }
    }

    sealed class RewardStatus {
        object Loading : RewardStatus()
        object Success : RewardStatus()
        data class Error(val message: String) : RewardStatus()
    }
} 
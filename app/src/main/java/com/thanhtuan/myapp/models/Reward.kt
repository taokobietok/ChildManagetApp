package com.thanhtuan.myapp.models

data class Reward(
    val rewardId: String = "",
    val name: String = "",
    val coinCost: Int = 0
) {
    companion object {
        const val COLLECTION_NAME = "rewards"
    }
} 
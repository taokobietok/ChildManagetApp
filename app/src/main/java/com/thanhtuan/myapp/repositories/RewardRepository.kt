package com.thanhtuan.myapp.repositories

import com.thanhtuan.myapp.models.Reward
import com.thanhtuan.myapp.services.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface RewardRepository {
    suspend fun redeemReward(childUid: String, reward: Reward): Result<Unit>
    fun getAvailableRewards(): List<Reward>
}

class RewardRepositoryImpl(private val firebaseService: FirebaseService) : RewardRepository {
    override suspend fun redeemReward(childUid: String, reward: Reward): Result<Unit> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseService.redeemReward(childUid, reward) { success ->
                if (success) {
                    continuation.resume(Result.success(Unit))
                } else {
                    continuation.resume(Result.failure(Exception("Failed to redeem reward")))
                }
            }
        }
    }

    override fun getAvailableRewards(): List<Reward> {
        return listOf(
            Reward("1", "Miễn làm việc nhà 1 ngày", 50),
            Reward("2", "Đi chơi ở công viên", 100),
            Reward("3", "Chơi game trong 30 phút", 150),
            Reward("4", "Đồ chơi mới", 200),
            Reward("5", "Xem phim vào buổi tối", 300)
        )
    }
} 
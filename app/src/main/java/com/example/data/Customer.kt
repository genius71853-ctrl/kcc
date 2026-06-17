package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val preferredAlcohol: String,
    val visitCount: Int = 1,
    val totalSpending: Long = 0,
    val lastVisitDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isVip: Boolean = false
)

@Entity(tableName = "notification_logs")
data class NotificationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val type: String, // "DORMANT" (휴면 30일 경과), "VIP_ANNI" (VIP 기념일 달성), "PREFERENCE_PROMO" (선호 음주 이벤트)
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val sentStatus: String = "PENDING" // "PENDING", "COMPLETED" (카카오톡 발송 완료), "NOTIFIED" (시스템 푸시 완료)
)

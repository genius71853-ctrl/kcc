package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM notification_logs ORDER BY createdAt DESC")
    fun getAllNotificationLogs(): Flow<List<NotificationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotificationLog(log: NotificationLog): Long

    @Update
    suspend fun updateNotificationLog(log: NotificationLog)

    @Query("DELETE FROM notification_logs WHERE id = :id")
    suspend fun deleteNotificationLogById(id: Long)

    @Query("DELETE FROM notification_logs")
    suspend fun clearAllLogs()
}

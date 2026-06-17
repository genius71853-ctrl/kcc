package com.example.data

import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val customerDao: CustomerDao) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allNotificationLogs: Flow<List<NotificationLog>> = customerDao.getAllNotificationLogs()

    suspend fun getCustomerById(id: Long): Customer? {
        return customerDao.getCustomerById(id)
    }

    suspend fun insertCustomer(customer: Customer): Long {
        return customerDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer)
    }

    suspend fun insertNotificationLog(log: NotificationLog): Long {
        return customerDao.insertNotificationLog(log)
    }

    suspend fun updateNotificationLog(log: NotificationLog) {
        customerDao.updateNotificationLog(log)
    }

    suspend fun deleteNotificationLogById(id: Long) {
        customerDao.deleteNotificationLogById(id)
    }

    suspend fun clearAllLogs() {
        customerDao.clearAllLogs()
    }
}

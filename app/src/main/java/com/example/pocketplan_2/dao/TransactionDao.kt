package com.example.pocketplan_2.dao

import androidx.room.*
import com.example.pocketplan_2.models.TransactionItem

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: TransactionItem)

    @Query("SELECT * FROM TransactionHistory ORDER BY date DESC")
    suspend fun getAllTransactions(): List<TransactionItem>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionItem)

    @Query("DELETE FROM TransactionHistory")
    suspend fun clearAllTransactions()
}

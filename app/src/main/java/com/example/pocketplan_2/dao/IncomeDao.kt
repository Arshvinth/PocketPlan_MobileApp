package com.example.pocketplan_2.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pocketplan_2.models.Income

@Dao
interface IncomeDao {
    @Insert
    suspend fun insertIncome(income: Income)

    @Update
    suspend fun updateIncome(income: Income)

    @Query("SELECT * FROM Income WHERE category = :category")
    suspend fun getIncomeByCategory(category: String): List<Income>

    @Query("SELECT * FROM Income")
    suspend fun getAllIncome(): List<Income>

    @Delete
    suspend fun deleteincome(income: Income)

}
package com.example.pocketplan_2.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pocketplan_2.models.Budget

@Dao
interface BudgetDao {
    @Insert
    suspend fun insertBudget(budget: Budget)

    @Query("SELECT * FROM Budget")
    suspend fun getAllBudget(): List<Budget>

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM Budget LIMIT 1")
    suspend fun getBudget(): Budget?

    @Update
    suspend fun updateBudget(budget: Budget)
}
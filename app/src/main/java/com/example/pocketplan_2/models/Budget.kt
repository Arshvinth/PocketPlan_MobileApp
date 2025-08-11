package com.example.pocketplan_2.models


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Parcelize
@Entity(tableName = "Budget")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var salary: Int,
    var business: Int,
    var otherIncome: Int,
    var food: Int,
    var transport: Int,
    var bills: Int,
    var entertainment: Int,
    var otherExpense: Int
) : Parcelable {
    fun getTotalIncome(): Int = salary + business + otherIncome
    fun getTotalExpenses(): Int = food + transport + bills + entertainment + otherExpense
    fun getBalance(): Int = getTotalIncome() - getTotalExpenses()
}


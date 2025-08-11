package com.example.pocketplan_2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
@Entity(tableName = "TransactionHistory")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val type: String  // "Income" or "Expense"
) : Parcelable

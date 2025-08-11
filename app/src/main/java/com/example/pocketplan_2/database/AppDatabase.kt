package com.example.pocketplan_2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pocketplan_2.dao.BudgetDao
import com.example.pocketplan_2.dao.ExpenseDao
import com.example.pocketplan_2.dao.IncomeDao
import com.example.pocketplan_2.dao.TransactionDao
import com.example.pocketplan_2.models.Budget
import com.example.pocketplan_2.models.Expense
import com.example.pocketplan_2.models.Income
import com.example.pocketplan_2.models.TransactionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Expense::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: ExpenseDatabase? = null

        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Database(entities = [Income::class], version = 1)
abstract class IncomeDatabase : RoomDatabase() {
    abstract fun IncomeDao(): IncomeDao

    companion object {
        @Volatile
        private var INSTANCE: IncomeDatabase? = null

        fun getDatabase(context: Context): IncomeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IncomeDatabase::class.java,
                    "income_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Database(entities = [TransactionItem::class], version = 1)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TransactionDatabase::class.java,
                    "transaction_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


@Database(entities = [Budget::class], version = 1)
abstract class BudgetDatabase : RoomDatabase() {

    abstract fun BudgetDao(): BudgetDao

    companion object {
        @Volatile private var INSTANCE: BudgetDatabase? = null

        fun getDatabase(context: Context): BudgetDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BudgetDatabase::class.java,
                    "Budget_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).BudgetDao().insertBudget(
                                    Budget(
                                        salary = 10000,
                                        business = 5000,
                                        otherIncome = 2000,
                                        food = 3000,
                                        transport = 1500,
                                        bills = 2500,
                                        entertainment = 1000,
                                        otherExpense = 500
                                    )
                                )
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.pocketplan_2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.adapters.BudgetAdapter
import com.example.pocketplan_2.R
import com.example.pocketplan_2.database.BudgetDatabase
import com.example.pocketplan_2.models.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Budget : AppCompatActivity() {

    private lateinit var balanceText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_budget)

        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Budget)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        balanceText = findViewById(R.id.BudgetAmtB)
        recyclerView = findViewById(R.id.BudgetRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadBudgetFromDatabase()

        // Tab Buttons
        findViewById<ImageButton>(R.id.BudgetTabSel).setOnClickListener {
            startActivity(Intent(this, Budget::class.java))
        }

        findViewById<ImageButton>(R.id.SavingsTab).setOnClickListener {
            startActivity(Intent(this, Savings::class.java))
        }

        findViewById<ImageButton>(R.id.HomeTab).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        findViewById<ImageButton>(R.id.backBtnB).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
    }

    private fun loadBudgetFromDatabase() {
        lifecycleScope.launch {
            val db = BudgetDatabase.getDatabase(this@Budget)
            var budget = db.BudgetDao().getBudget()

            //Set default values while budget table is null
            if (budget == null) {
                budget = Budget(
                    salary = 10000,
                    business = 5000,
                    otherIncome = 2000,
                    food = 3000,
                    transport = 1500,
                    bills = 2500,
                    entertainment = 1000,
                    otherExpense = 500
                )
                db.BudgetDao().insertBudget(budget)
            }

            displayBalance(budget)

            adapter = BudgetAdapter(budget) { updatedBudget ->
                displayBalance(updatedBudget)
                updateBudgetItem(updatedBudget)
            }

            recyclerView.adapter = adapter
        }
    }

    private fun displayBalance(item: Budget) {
        balanceText.text = "Rs: ${item.getBalance()}"
    }

    private fun updateBudgetItem(item: Budget) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = BudgetDatabase.getDatabase(this@Budget)
            db.BudgetDao().updateBudget(item)
        }
    }
}
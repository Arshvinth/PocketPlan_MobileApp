package com.example.pocketplan_2
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.adapters.ExpenseAdapter
import com.example.pocketplan_2.adapters.IncomeAdapter
import com.example.pocketplan_2.database.IncomeDatabase
import kotlinx.coroutines.launch

class ViewIncome : AppCompatActivity() {

    private lateinit var db: IncomeDatabase
    private lateinit var adapter: IncomeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)

        setContentView(R.layout.activity_view_income)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ViewIncome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = IncomeDatabase.getDatabase(this)

        val totalIncomeTextView = findViewById<TextView>(R.id.TotalExpVI)
        lifecycleScope.launch {
            val allIncome = db.IncomeDao().getAllIncome()
            val totalIncome = allIncome.sumOf { it.amount }
            totalIncomeTextView.text = "Rs: $totalIncome"
        }

        spinner = findViewById(R.id.spinnerCategoryVE)
        searchButton = findViewById(R.id.SearchBtnVE)
        recyclerView = findViewById(R.id.incomeRecyclerView)
        val totalAmountTextView = findViewById<TextView>(R.id.TotalExpenseVE)

        adapter = IncomeAdapter(emptyList(), { income, _ ->
            // When the edit button is clicked, pass the selected expense to UpdateExpense activity
            val intent = Intent(this, UpdateIncome::class.java).apply {
                putExtra("income", income) // Pass the expense object
            }
            startActivity(intent)
        }, { income, _ ->
            // Handle delete click if needed
            lifecycleScope.launch {
                db.IncomeDao().deleteincome(income)
                // Refresh the list after deleting
                val updatedIncome = db.IncomeDao().getIncomeByCategory(spinner.selectedItem.toString())
                adapter.updateList(updatedIncome)

                val totalAmount = updatedIncome.sumOf { it.amount }
                totalAmountTextView.text = "Total: Rs $totalAmount"
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        searchButton.setOnClickListener {
            val selectedCategory = spinner.selectedItem.toString()
            lifecycleScope.launch {
                val filteredIncome = db.IncomeDao().getIncomeByCategory(selectedCategory)
                adapter.updateList(filteredIncome)

                //Calculate total cost
                val allIncomes = db.IncomeDao().getAllIncome()
                val totalIncome = allIncomes.sumOf { it.amount }
                totalIncomeTextView.text = "Rs: $totalIncome"

                // Calculate total
                val totalAmount = filteredIncome.sumOf { it.amount }
                totalAmountTextView.text = "Total: Rs $totalAmount"
            }
        }

        val BudgetTab: ImageButton = findViewById(R.id.BudhetTab);
        BudgetTab.setOnClickListener{
            val intent = Intent(this, Budget::class.java);
            startActivity(intent);
        }

        val SavingsTab: ImageButton = findViewById(R.id.SavingsTab);
        SavingsTab.setOnClickListener{
            val intent = Intent(this, Savings::class.java);
            startActivity(intent);
        }

        val HomeTab: ImageButton = findViewById(R.id.HomeTab);
        HomeTab.setOnClickListener{
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }

        val BackBtnVI: ImageButton = findViewById(R.id.backBtnVI);
        BackBtnVI.setOnClickListener{
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }
    }
}
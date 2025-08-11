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
import com.example.pocketplan_2.database.ExpenseDatabase
import kotlinx.coroutines.launch

class ViewExpense : AppCompatActivity() {

    private lateinit var db: ExpenseDatabase
    private lateinit var adapter: ExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var searchButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)

        setContentView(R.layout.activity_view_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ViewExpense)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = ExpenseDatabase.getDatabase(this)

        val totalExpenseTextView = findViewById<TextView>(R.id.TotalExpVI)
        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()
            val totalExpense = allExpenses.sumOf { it.amount }
            totalExpenseTextView.text = "Rs: $totalExpense"
        }

        spinner = findViewById(R.id.spinnerCategoryVI)
        searchButton = findViewById(R.id.SearchBtnVE)
        recyclerView = findViewById(R.id.ExpenseRecyclerView)
        val totalAmountTextView = findViewById<TextView>(R.id.TotalExpenseVE)

        adapter = ExpenseAdapter(emptyList(), { expense, _ ->
            // When the edit button is clicked, pass the selected expense to UpdateExpense activity
            val intent = Intent(this, updateExpense::class.java).apply {
                putExtra("expense", expense) // Pass the expense object
            }
            startActivity(intent)
        }, { expense, _ ->
            // Handle delete click if needed
            lifecycleScope.launch {
                db.expenseDao().deleteExpense(expense)
                // Refresh the list after deleting
                val updatedExpenses = db.expenseDao().getExpensesByCategory(spinner.selectedItem.toString())
                adapter.updateList(updatedExpenses)

                val totalAmount = updatedExpenses.sumOf { it.amount }
                totalAmountTextView.text = "Total: Rs $totalAmount"
            }
        })


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        searchButton.setOnClickListener {
            val selectedCategory = spinner.selectedItem.toString()
            lifecycleScope.launch {
                val filteredExpenses = db.expenseDao().getExpensesByCategory(selectedCategory)
                adapter.updateList(filteredExpenses)

                //Calculate total cost
                val allExpenses = db.expenseDao().getAllExpenses()
                val totalExpense = allExpenses.sumOf { it.amount }
                totalExpenseTextView.text = "Rs: $totalExpense"

                // Calculate total
                val totalAmount = filteredExpenses.sumOf { it.amount }
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

        val BackBtnVE: ImageButton = findViewById(R.id.backBtnVE);
        BackBtnVE.setOnClickListener{
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }
    }
}
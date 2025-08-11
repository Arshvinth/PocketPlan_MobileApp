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

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.database.IncomeDatabase
import kotlinx.coroutines.launch

class Savings : AppCompatActivity() {
    private lateinit var db2: IncomeDatabase
    private lateinit var db: ExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)

        setContentView(R.layout.activity_savings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Savings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val IncomeView = findViewById<TextView>(R.id.IncomeTxtS)
        val ExpenseView = findViewById<TextView>(R.id.TotalExpS)
        val balanceTextView = findViewById<TextView>(R.id.SavingsS)
        val greetTxt = findViewById<TextView>(R.id.totalSavedS)

        db = ExpenseDatabase.getDatabase(this)
        db2 = IncomeDatabase.getDatabase(this)

        //Get income, Expense and budget information from DB
        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()
            val totalExpense = allExpenses.sumOf { it.amount }

            val allIncome = db2.IncomeDao().getAllIncome()
            val totalIncome = allIncome.sumOf { it.amount }

            IncomeView.text = "$totalIncome"
            ExpenseView.text = "$totalExpense"

            val savings = totalIncome - totalExpense
            balanceTextView.text = "$savings"
            greetTxt.text = "Rs.$savings"
        }

        val BudhetTabS: ImageButton = findViewById(R.id.BudhetTab);
        BudhetTabS.setOnClickListener{
            val intent = Intent(this, Budget::class.java);
            startActivity(intent);
        }

        val SavingsTabSel: ImageButton = findViewById(R.id.SavingsTabSel);
        SavingsTabSel.setOnClickListener{
            val intent = Intent(this, Savings::class.java);
            startActivity(intent);
        }

        val HomeTab: ImageButton = findViewById(R.id.HomeTab);
        HomeTab.setOnClickListener{
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }

        val BackBtnS: ImageButton = findViewById(R.id.backBtnS)
        BackBtnS.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }
    }
}
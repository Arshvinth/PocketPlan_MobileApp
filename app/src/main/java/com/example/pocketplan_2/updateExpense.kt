package com.example.pocketplan_2
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.models.Expense
import kotlinx.coroutines.launch

import java.util.Calendar


class updateExpense : AppCompatActivity() {

    private lateinit var expense: Expense

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)

        setContentView(R.layout.activity_update_expense)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.updateExpense)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtTitle = findViewById<EditText>(R.id.edtTitleUE)
        val edtAmount = findViewById<EditText>(R.id.edtAmountUE)
        val edtDate = findViewById<EditText>(R.id.edtDateUE)
        val btnUpdate = findViewById<Button>(R.id.UpdateBtnUE)
        val db = ExpenseDatabase.getDatabase(this)

        // Get passed Expense
        expense = intent.getParcelableExtra("expense")!!

        // Fill form with data
        edtTitle.setText(expense.title)
        edtAmount.setText(expense.amount.toString())
        edtDate.setText(expense.date)

        // Show DatePicker on date field click
        edtDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, day ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    edtDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.maxDate = System.currentTimeMillis() // Disallow future dates
            val minDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
            datePicker.datePicker.minDate = minDate.timeInMillis // Only 1 month old
            datePicker.show()
        }

        //Update button click
        btnUpdate.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val amountStr = edtAmount.text.toString().trim()
            val date = edtDate.text.toString().trim()

            //Validations
            if (title.isEmpty()) {
                edtTitle.error = "Title required"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                edtAmount.error = "Enter valid amount"
                return@setOnClickListener
            }

            if (date.isEmpty()) {
                edtDate.error = "Date required"
                return@setOnClickListener
            }

            // Final object
            val updatedExpense = expense.copy(
                title = title,
                amount = amount,
                date = date
            )

            //DB update
            lifecycleScope.launch {
                db.expenseDao().updateExpense(updatedExpense)
                Toast.makeText(this@updateExpense, "Expense Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        //Navigation buttons
        findViewById<ImageButton>(R.id.BudhetTab).setOnClickListener {
            startActivity(Intent(this, Budget::class.java))
        }

        findViewById<ImageButton>(R.id.SavingsTab).setOnClickListener {
            startActivity(Intent(this, Savings::class.java))
        }

        findViewById<ImageButton>(R.id.HomeTab).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }

        findViewById<ImageButton>(R.id.backBtnUE).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
    }
}
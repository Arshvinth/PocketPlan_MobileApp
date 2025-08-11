package com.example.pocketplan_2
import android.app.DatePickerDialog
import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.database.IncomeDatabase
import com.example.pocketplan_2.database.TransactionDatabase
import com.example.pocketplan_2.models.Income
import com.example.pocketplan_2.models.TransactionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class AddIncome : AppCompatActivity() {

    private lateinit var db: IncomeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)
        setContentView(R.layout.activity_add_income)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AddIncome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database
        db = IncomeDatabase.getDatabase(this)

        // UI components
        val edtTitleAI: EditText = findViewById(R.id.edtTitleAI)
        val spinnerAI: Spinner = findViewById(R.id.spinnerAI)
        val edtAmountAI: EditText = findViewById(R.id.edtAmountAI)
        val edtDateAI: EditText = findViewById(R.id.edtDateAI)
        val AddIncomeBtnAI: Button = findViewById(R.id.AddIncomeBtnAI)

        // Date picker
        edtDateAI.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate =
                        String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    edtDateAI.setText(formattedDate)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Save Income button
        AddIncomeBtnAI.setOnClickListener {
            val title = edtTitleAI.text.toString().trim()
            val category = spinnerAI.selectedItem.toString()
            val amountText = edtAmountAI.text.toString().trim()
            val dateText = edtDateAI.text.toString().trim()

            // Input validation: title
            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Input validation: category
            if (category.equals("Select Category", true)) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Input validation: amount
            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Input validation: date
            if (dateText.isEmpty()) {
                Toast.makeText(this, "Date is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate date format and range
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.isLenient = false

            val selectedDate: Date = try {
                formatter.parse(dateText)!!
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today = Calendar.getInstance().time
            val oneMonthAgo = Calendar.getInstance().apply {
                add(Calendar.MONTH, -1)
            }.time

            if (selectedDate.after(today)) {
                Toast.makeText(this, "Date cannot be in the future", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDate.before(oneMonthAgo)) {
                Toast.makeText(this, "Date must be within the last 1 month", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create and insert Income object
            val income = Income(
                title = title,
                category = category,
                amount = amount,
                date = dateText
            )

            //Add to transaction history
            lifecycleScope.launch(Dispatchers.IO) {
                val transaction = TransactionItem(
                    title = title,
                    amount = amount,
                    date = dateText,
                    category = category,
                    type = "Income"
                )

                val db = TransactionDatabase.getDatabase(applicationContext)
                db.transactionDao().insertTransaction(transaction)
            }


            lifecycleScope.launch(Dispatchers.IO) {
                db.IncomeDao().insertIncome(income)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddIncome, "Income added!", Toast.LENGTH_SHORT).show()
                    edtTitleAI.text.clear()
                    edtAmountAI.text.clear()
                    edtDateAI.text.clear()
                    spinnerAI.setSelection(0)
                }
            }
        }

        // Navigation
        findViewById<ImageButton>(R.id.BudhetTab).setOnClickListener {
            startActivity(Intent(this, Budget::class.java))
        }
        findViewById<ImageButton>(R.id.SavingsTab).setOnClickListener {
            startActivity(Intent(this, Savings::class.java))
        }
        findViewById<ImageButton>(R.id.HomeTab).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
        findViewById<ImageButton>(R.id.backBtn2).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
    }
}
package com.example.pocketplan_2
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
//
import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.database.BudgetDatabase
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.database.TransactionDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.pocketplan_2.models.Budget
import com.example.pocketplan_2.models.Expense
import com.example.pocketplan_2.models.TransactionItem

class AddExpense : AppCompatActivity() {
    private lateinit var db: ExpenseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)
        setContentView(R.layout.activity_add_expense)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.AddExpense)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get database
        db = ExpenseDatabase.getDatabase(this)

        val estTitleE: EditText = findViewById(R.id.estTitleE)
        val spinnerE: Spinner = findViewById(R.id.spinnerE)
        val edtAmountE: EditText = findViewById(R.id.edtAmountE)
        val edtDateE: EditText = findViewById(R.id.edtDateE)
        val btnSaveExpense: Button = findViewById(R.id.AddExpenseBtn)

        edtDateE.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate =
                        String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    edtDateE.setText(formattedDate)
                },
                year, month, day
            )

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            calendar.add(Calendar.MONTH, -1)
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        btnSaveExpense.setOnClickListener {
            val title = estTitleE.text.toString().trim()
            val category = spinnerE.selectedItem.toString()
            val amountText = edtAmountE.text.toString().trim()
            val dateText = edtDateE.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerE.selectedItemPosition == 0 || category.equals("Select Category", true)) {
                Toast.makeText(this, "Please select a valid category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0.0) {
                Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val inputDate: LocalDate
            try {
                inputDate = LocalDate.parse(dateText, formatter)
            } catch (e: Exception) {
                Toast.makeText(this, "Enter a valid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val today = LocalDate.now()
            val oneMonthAgo = today.minusMonths(1)

            if (inputDate.isAfter(today)) {
                Toast.makeText(this, "Date cannot be in the future", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputDate.isBefore(oneMonthAgo)) {
                Toast.makeText(this, "Date can only be within the last month", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate against budget
            lifecycleScope.launch(Dispatchers.IO) {
                val expenseDao = db.expenseDao()
                val totalSpent = expenseDao.getTotalByCategory(category) ?: 0.0

                val budgetDb = BudgetDatabase.getDatabase(this@AddExpense)
                val budget = budgetDb.BudgetDao().getBudget()

                if (budget != null) {
                    val categoryBudget = when (category) {
                        "Food" -> budget.food
                        "Transport" -> budget.transport
                        "Bills" -> budget.bills
                        "Entertainment" -> budget.entertainment
                        "Other" -> budget.otherExpense
                        else -> Int.MAX_VALUE // no limit for unknown categories
                    }

                    if ((totalSpent.toDouble() + amount) > categoryBudget) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@AddExpense,
                                "Budget exceeded for $category expense!",
                                Toast.LENGTH_LONG
                            ).show()
                            showBudgetExceededNotification(category);
                        }
                        return@launch
                    }
                }

                // Insert into DB
                val newExpense = Expense(
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
                        type = "Expense"
                    )

                    val db = TransactionDatabase.getDatabase(applicationContext)
                    db.transactionDao().insertTransaction(transaction)
                }

                expenseDao.insertExpense(newExpense)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddExpense, "Expense saved!", Toast.LENGTH_SHORT).show()
                    estTitleE.text.clear()
                    edtAmountE.text.clear()
                    edtDateE.text.clear()
                    spinnerE.setSelection(0)
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
        findViewById<ImageButton>(R.id.BackBtn).setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
        }
    }

    //Notification if budget exceeded
    private fun showBudgetExceededNotification(category: String) {
        val channelId = "budget_alert_channel"
        val channelName = "Budget Alerts"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // or your custom bell icon
            .setContentTitle("Budget Limit Exceeded!")
            .setContentText("Expense budget exceeded for $category!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(category.hashCode(), builder.build())
    }
}
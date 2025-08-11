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
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan_2.R
import com.example.pocketplan_2.adapters.ExpenseAdapter
import com.example.pocketplan_2.adapters.IncomeAdapter
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.database.IncomeDatabase
import com.example.pocketplan_2.models.Expense
import com.example.pocketplan_2.models.Income
import kotlinx.coroutines.launch
import java.util.Calendar

class UpdateIncome : AppCompatActivity() {

    private lateinit var income: Income

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)
        setContentView(R.layout.activity_update_income)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.UpdateIncome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtTitle = findViewById<EditText>(R.id.edtTitleUI)
        val edtAmount = findViewById<EditText>(R.id.edtAmountUI)
        val edtDate = findViewById<EditText>(R.id.edtDateUI)
        val btnUpdate = findViewById<Button>(R.id.UpdateBtnUI)
        val db = IncomeDatabase.getDatabase(this)

        // Get passed Income
        income = intent.getParcelableExtra("income")!!

        // Fill form with data
        edtTitle.setText(income.title)
        edtAmount.setText(income.amount.toString())
        edtDate.setText(income.date)

        //Show DatePicker on date field click
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

        // Update button click
        btnUpdate.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val amountStr = edtAmount.text.toString().trim()
            val date = edtDate.text.toString().trim()

            // Validations
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
            val updatedIncome = income.copy(
                title = title,
                amount = amount,
                date = date
            )

            //DB update
            lifecycleScope.launch {
                db.IncomeDao().updateIncome(updatedIncome)
                Toast.makeText(this@UpdateIncome, "Income Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        val BudgetTab: ImageButton = findViewById(R.id.BudhetTab);
        BudgetTab.setOnClickListener {
            val intent = Intent(this, Budget::class.java);
            startActivity(intent);
        }

        val SavingsTab: ImageButton = findViewById(R.id.SavingsTab);
        SavingsTab.setOnClickListener {
            val intent = Intent(this, Savings::class.java);
            startActivity(intent);
        }

        val HomeTab: ImageButton = findViewById(R.id.HomeTab);
        HomeTab.setOnClickListener {
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }

        val BackBtnUI: ImageButton = findViewById(R.id.backBtn);
        BackBtnUI.setOnClickListener {
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }
    }
}
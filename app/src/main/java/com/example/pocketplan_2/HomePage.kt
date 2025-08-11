package com.example.pocketplan_2
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.database.IncomeDatabase
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity() {
    private lateinit var db2: IncomeDatabase
    private lateinit var db: ExpenseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.statusBarColor = ContextCompat.getColor(this, R.color.StatusBar)
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.HomePage)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Display Username
        val username = intent.getStringExtra("USERNAME")
        val UsernameView = findViewById<TextView>(R.id.UserNameH)
        UsernameView.text = "$username"

        //Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            } else {
                showNotification()
            }
        } else {
            showNotification()
        }

        db = ExpenseDatabase.getDatabase(this)
        db2 = IncomeDatabase.getDatabase(this)

        val IncomeView = findViewById<TextView>(R.id.TotalIncH)
        val ExpenseView = findViewById<TextView>(R.id.totalExpenseH)
        val savingsTxt = findViewById<TextView>(R.id.savingsH)

        lifecycleScope.launch {
            val allExpenses = db.expenseDao().getAllExpenses()
            val totalExpense = allExpenses.sumOf { it.amount }

            val allIncome = db2.IncomeDao().getAllIncome()
            val totalIncome = allIncome.sumOf { it.amount }

            IncomeView.text = "$totalIncome"
            ExpenseView.text = "$totalExpense"

            val savings = totalIncome - totalExpense
            savingsTxt.text = "$savings"
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

        val HomeTabSel: ImageButton = findViewById(R.id.HomeTabSel);
        HomeTabSel.setOnClickListener{
            val intent = Intent(this, HomePage::class.java);
            startActivity(intent);
        }

        val AddIncomeBtnH: Button = findViewById(R.id.AddIncomeBtnH);
        AddIncomeBtnH.setOnClickListener{
            val intent = Intent(this, AddIncome::class.java);
            startActivity(intent);
        }

        val ViewIncomeBtnH: Button = findViewById(R.id.ViewIncomeBtnH);
        ViewIncomeBtnH.setOnClickListener{
            val intent = Intent(this, ViewIncome::class.java);
            startActivity(intent);
        }

        val AddExpenseBtnH: Button = findViewById(R.id.AddExpenseBtnH);
        AddExpenseBtnH.setOnClickListener{
            val intent = Intent(this, AddExpense::class.java);
            startActivity(intent);
        }

        val ViewExpenseBtnH: Button = findViewById(R.id.ViewExpenseBtnH);
        ViewExpenseBtnH.setOnClickListener{
            val intent = Intent(this, ViewExpense::class.java);
            startActivity(intent);
        }

        val ViewBudegetH: Button = findViewById(R.id.ViewBudegetH);
        ViewBudegetH.setOnClickListener{
            val intent = Intent(this, Budget::class.java);
            startActivity(intent);
        }

        val ViewSavingsH: Button = findViewById(R.id.ViewSavingsH);
        ViewSavingsH.setOnClickListener{
            val intent = Intent(this, Savings::class.java);
            startActivity(intent);
        }

        val HistoryBtnH: ImageButton = findViewById(R.id.HistoryBtnH);
        HistoryBtnH.setOnClickListener{
            val intent = Intent(this, TransactionHistory::class.java);
            startActivity(intent);
        }

        val HistoryTxtH: TextView = findViewById(R.id.HistoryTxtH);
        HistoryTxtH.setOnClickListener{
            val intent = Intent(this, TransactionHistory::class.java);
            startActivity(intent);
        }
    }
    //Notifications
    private fun showNotification() {
        val channelId = "daily_budget_reminder"
        val channelName = "Daily Budget Notification"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // replace with your own icon
            .setContentTitle("Budget Reminder")
            .setContentText("Add your daily income and expenses")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(100, builder.build())
    }
}
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
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import androidx.lifecycle.lifecycleScope
import com.example.pocketplan_2.R
import com.example.pocketplan_2.adapters.ExpenseAdapter
import com.example.pocketplan_2.adapters.TransactionAdapter
import com.example.pocketplan_2.database.ExpenseDatabase
import com.example.pocketplan_2.database.TransactionDatabase
import com.example.pocketplan_2.models.TransactionItem
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream

class TransactionHistory : AppCompatActivity() {
    private lateinit var db: TransactionDatabase
    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var BackUpBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.statusBarColor = ContextCompat.getColor(this, R.color.LightBlue)

        setContentView(R.layout.activity_transaction_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.TransactionHistory)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database
        db = TransactionDatabase.getDatabase(this)

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.TransactionsRecyclerView)  //
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(emptyList()) // initially empty list
        recyclerView.adapter = adapter

        // Fetch transaction history from database
        lifecycleScope.launch(Dispatchers.IO) {
            val transactions = db.transactionDao().getAllTransactions() // Get all transactions
            withContext(Dispatchers.Main) {
                // Update RecyclerView with the fetched transactions
                adapter = TransactionAdapter(transactions)
                recyclerView.adapter = adapter
            }
        }

        // Handle search button click
        BackUpBtn = findViewById(R.id.backupBtn)
        BackUpBtn.setOnClickListener {

            lifecycleScope.launch(Dispatchers.IO) {
                val transactions = db.transactionDao().getAllTransactions()

                // Save to internal storage
                saveTransactionsToInternalStorage(transactions)
            }
        }

        // Handle tabs for navigation
        val BudgetTab: ImageButton = findViewById(R.id.BudhetTab)
        BudgetTab.setOnClickListener {
            val intent = Intent(this, Budget::class.java)
            startActivity(intent)
        }

        val SavingsTab: ImageButton = findViewById(R.id.SavingsTab)
        SavingsTab.setOnClickListener {
            val intent = Intent(this, Savings::class.java)
            startActivity(intent)
        }

        val HomeTab: ImageButton = findViewById(R.id.HomeTab)
        HomeTab.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

        val BackBtnTH: ImageButton = findViewById(R.id.backBtnTH)
        BackBtnTH.setOnClickListener {
            val intent = Intent(this, HomePage::class.java)
            startActivity(intent)
        }

    }

    // Function to save transactions to internal storage
    private fun saveTransactionsToInternalStorage(transactions: List<TransactionItem>) {
        // Use GsonBuilder to enable pretty printing
        val gson = GsonBuilder().setPrettyPrinting().create()

        // Convert the transactions to a pretty-printed JSON string
        val json = gson.toJson(transactions)

        try {
            // Open a file output stream to write to internal storage
            val fileOutputStream: FileOutputStream = openFileOutput("transaction_history.json", MODE_PRIVATE)
            fileOutputStream.write(json.toByteArray())
            fileOutputStream.close()

            //Notification
            createNotificationChannel();
            showBackupNotification();
            // Provide feedback to the user
            Toast.makeText(this, "Transaction history saved!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "backup_channel",
                "Backup Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows notifications after successful backup"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showBackupNotification() {
        // Only show notification if permission is granted
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            val builder = NotificationCompat.Builder(this, "backup_channel")
                .setSmallIcon(android.R.drawable.stat_notify_sync) // Use your icon
                .setContentTitle("Backup Successful")
                .setContentText("Expenses backed up successfully.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(this)) {
                notify(2001, builder.build())
            }

        } else {
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}
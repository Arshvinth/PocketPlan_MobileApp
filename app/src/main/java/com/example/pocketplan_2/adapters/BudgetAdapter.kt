package com.example.pocketplan_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan_2.models.Budget
import com.example.pocketplan_2.R

class BudgetAdapter(
    private var item: Budget,
    private val onEditClick: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val salary: EditText = itemView.findViewById(R.id.edtSalaryB)
        val business: EditText = itemView.findViewById(R.id.edtBusinessB)
        val otherIncome: EditText = itemView.findViewById(R.id.edtOtherB)
        val food: EditText = itemView.findViewById(R.id.edtFoodB)
        val transport: EditText = itemView.findViewById(R.id.edtTransportB)
        val bills: EditText = itemView.findViewById(R.id.edtBillsB)
        val entertainment: EditText = itemView.findViewById(R.id.edtEntertainB)
        val otherExpense: EditText = itemView.findViewById(R.id.edtOtherBdd)
        val editBtn: ImageButton = itemView.findViewById(R.id.edtBtnB)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.salary.setText(item.salary.toString())
        holder.business.setText(item.business.toString())
        holder.otherIncome.setText(item.otherIncome.toString())
        holder.food.setText(item.food.toString())
        holder.transport.setText(item.transport.toString())
        holder.bills.setText(item.bills.toString())
        holder.entertainment.setText(item.entertainment.toString())
        holder.otherExpense.setText(item.otherExpense.toString())

            holder.editBtn.setOnClickListener {
            item.salary = holder.salary.text.toString().toIntOrNull() ?: 0
            item.business = holder.business.text.toString().toIntOrNull() ?: 0
            item.otherIncome = holder.otherIncome.text.toString().toIntOrNull() ?: 0
            item.food = holder.food.text.toString().toIntOrNull() ?: 0
            item.transport = holder.transport.text.toString().toIntOrNull() ?: 0
            item.bills = holder.bills.text.toString().toIntOrNull() ?: 0
            item.entertainment = holder.entertainment.text.toString().toIntOrNull() ?: 0
            item.otherExpense = holder.otherExpense.text.toString().toIntOrNull() ?: 0

            onEditClick(item)
        }
    }

    override fun getItemCount(): Int = 1 // only one record
}
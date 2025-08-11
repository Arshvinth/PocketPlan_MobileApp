package com.example.pocketplan_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan_2.R
import com.example.pocketplan_2.models.Expense

class ExpenseAdapter(
    private var ExpenseList: List<Expense>,
    private val onEditClick: (Expense, Int) -> Unit,
    private val onDeleteClick: (Expense, Int) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.DateVE1)
        val titleView: TextView = itemView.findViewById(R.id.titleVE1)
        val amountView: TextView = itemView.findViewById(R.id.AmountVE1)
        val editBtn: ImageButton = itemView.findViewById(R.id.edtBtnVE1)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.DltBtnVE1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val item = ExpenseList[position]
        holder.dateView.text = item.date
        holder.titleView.text = item.title
        holder.amountView.text = item.amount.toString()

        holder.editBtn.setOnClickListener { onEditClick(item, position) }
        holder.deleteBtn.setOnClickListener { onDeleteClick(item, position) }
    }

    override fun getItemCount(): Int = ExpenseList.size


    fun updateList(newList: List<Expense>) {
        ExpenseList = newList
        notifyDataSetChanged()
    }
}


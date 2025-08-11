package com.example.pocketplan_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan_2.R
import com.example.pocketplan_2.models.TransactionItem

class TransactionAdapter(private val transactions: List<TransactionItem>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.titleT1)
        val amount: TextView = view.findViewById(R.id.AmountT1)
        val date: TextView = view.findViewById(R.id.DateT1)
        val category: TextView = view.findViewById(R.id.CategoryT1)
        val type: TextView = view.findViewById(R.id.TypeT1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]
        holder.title.text = item.title
        holder.amount.text = "${item.amount}"
        holder.date.text = item.date
        holder.category.text = item.category
        holder.type.text = item.type
    }

    override fun getItemCount() = transactions.size
}
package com.example.pocketplan_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketplan_2.R
import com.example.pocketplan_2.models.Income


class IncomeAdapter(
    private var incomeList: List<Income>,
    private val onEditClick: (Income, Int) -> Unit,
    private val onDeleteClick: (Income, Int) -> Unit
) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateView: TextView = itemView.findViewById(R.id.DateVI1)
        val titleView: TextView = itemView.findViewById(R.id.titleVI1)
        val amountView: TextView = itemView.findViewById(R.id.AmountVI1)
        val editBtn: ImageButton = itemView.findViewById(R.id.edtBtnVI)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.dltBtnVI1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_income, parent, false)
        return IncomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val item = incomeList[position]
        holder.dateView.text = item.date
        holder.titleView.text = item.title
        holder.amountView.text = item.amount.toString()

        holder.editBtn.setOnClickListener { onEditClick(item, position) }
        holder.deleteBtn.setOnClickListener { onDeleteClick(item, position) }
    }

    override fun getItemCount(): Int = incomeList.size


    fun updateList(newList: List<Income>) {
        incomeList = newList
        notifyDataSetChanged()
    }
}
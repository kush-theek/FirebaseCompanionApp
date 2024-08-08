package com.theektek.validatesub.app.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theektek.validatesub.R

class CustomerAdapter(private val customerList: List<CustomerModel>) :
    RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerId: TextView = itemView.findViewById(R.id.customer_id)
        val customerName: TextView = itemView.findViewById(R.id.customer_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_recycler_item, parent, false)
        return CustomerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = customerList[position]
        holder.customerId.text = customer.customerId
        holder.customerName.text = customer.customerName
    }

    override fun getItemCount() = customerList.size
}

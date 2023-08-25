package com.kabu.myapplication.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kabu.myapplication.DateTimeUtils
import com.kabu.myapplication.R
import com.kabu.myapplication.model.TicketResponse

interface OnItemClickListener {
    fun onItemClick(item: TicketResponse)
}

class ListDetailAdapter(private val list: List<TicketResponse>) :
    RecyclerView.Adapter<ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.detailt_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.date.text = "Ngày khám: ${DateTimeUtils.dayDisplay(model.issueDateTime)}"
        holder.userId.text = "Bệnh Nhân: ${model.hiCardNo}"
        holder.userName.text = "Bệnh Nhân:  ${model.patientName}"
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(list[position])
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val userId: TextView
    val userName: TextView
    val date: TextView

    init {
    }

    init {
        userId = view.findViewById(R.id.txtUseId)
        userName = view.findViewById(R.id.txtName)
        date = view.findViewById(R.id.txtDate)
    }
}
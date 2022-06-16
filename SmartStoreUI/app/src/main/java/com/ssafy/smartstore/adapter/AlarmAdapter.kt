package com.ssafy.smartstore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.smartstore.R

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    var alarmList = mutableListOf<String>()

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAlarm: TextView = itemView.findViewById(R.id.tv_recycler_alarm)
        val btnRemove : ImageButton = itemView.findViewById(R.id.btn_alarm_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.recycler_alarm_item, parent, false)
        return AlarmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.tvAlarm.text = alarmList[position]

        holder.btnRemove.setOnClickListener {
            onRemoveButtonClick.onRemoveClick(holder.itemView,position)
        }

    }

    override fun getItemCount(): Int {
        return alarmList.size
    }

    lateinit var onRemoveButtonClick: OnRemoveButtonClick

    interface OnRemoveButtonClick{
        fun onRemoveClick(view: View, position: Int)
    }
}
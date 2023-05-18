package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityNewRoutineBinding

interface OnItemClickListener {
    fun onItemClick(position: Int)
}


class RoutineFragmentAdapter(
    val context: Context,
    private val list: ArrayList<RoutineModelClass>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<RoutineFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val routineName: TextView? =
            view.findViewById<TextView>(R.id.routine_name)
        val lastRun: TextView? = view.findViewById<TextView>(R.id.last_run)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.second_routine_list,
                parent,
                false
            ), itemClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = list[position]
        holder.routineName?.text = record.routineName
        holder.lastRun?.text = "Last Run: " + record.lastRun
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
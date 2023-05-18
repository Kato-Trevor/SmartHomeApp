package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoutineAdapter(val context: Context, val list: ArrayList<RoutineModelClass>): RecyclerView.Adapter<RoutineAdapter.ViewHolder>() {


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val RoutineName =
            view.findViewById<TextView>(R.id.routine_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.first_routine_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list.get(position)
        holder.RoutineName.text = item.routineName
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
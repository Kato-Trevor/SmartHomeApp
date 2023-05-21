package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FavoritesFragmentAdapter(
    val context: Context,
    private val list: ArrayList<FavoritesModel>,
    private val itemClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<FavoritesFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View, itemClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(view) {
        val routineName: TextView? =
            view.findViewById<TextView>(R.id.routine_name)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.favorites_routine_list,
                parent,
                false
            ), itemClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = list[position]
        holder.routineName?.text = record.routineName
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
package com.example.classcollab.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R

class CreatedClassesAdapter(private val dataset: Array<String>) : RecyclerView.Adapter<CreatedClassesAdapter.ViewHolder>()  {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val textView: TextView

        init {
            textView = view.findViewById(R.id.each_class_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_createdclass_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text=dataset[position]
    }

    override fun getItemCount() = dataset.size
}
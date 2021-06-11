package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R

class CreatedClassesAdapter(
        private var dataset: MutableList<String>,
        private val context: Context?,
        private var listener: OnItemClickListener
) :
    RecyclerView.Adapter<CreatedClassesAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.each_createdclass_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text=getDataset().get(position)
        println(getDataset().get(position))
    }

    override fun getItemCount():Int{
         if(dataset.size==0)
             return 0
         return getDataset().size
    }

    fun getDataset(): MutableList<String> {

        return dataset
    }

    fun setDataset(passedDS: MutableList<String>){

        dataset = passedDS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView = view.findViewById<TextView>(R.id.each_class_name)
        init{
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position:Int = absoluteAdapterPosition
            if(position != RecyclerView.NO_POSITION)
            {
                listener.onItemClick(position)
            }

        }
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}
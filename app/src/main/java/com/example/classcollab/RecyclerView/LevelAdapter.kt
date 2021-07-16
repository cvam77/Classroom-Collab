package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R

/*Usually if you go to the second or third level deep inside the app, the screen is divided into two parts - top part is folder part and the bottom part is
question part.
LevelAdapter is an adapter where you pass a mutable list of folder names (top part) and the recycle view uses this adapter to show the folder names
 */
class LevelAdapter (
        private var dataset: MutableList<String>,
        private val context: Context?,
        private var listener: LevelAdapter.OnItemClickListener
        ) : RecyclerView.Adapter<LevelAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.each_createdclass_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var string = getDataset().get(position)
        holder.textView.text = string
    }

    override fun getItemCount(): Int {
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
                val string = getDataset().get(position)
                listener.onItemClick(string)
            }

        }
    }

    interface OnItemClickListener{
        abstract fun onItemClick(position: String)
    }
}
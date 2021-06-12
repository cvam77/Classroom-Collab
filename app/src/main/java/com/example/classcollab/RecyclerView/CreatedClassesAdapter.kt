package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreatedClassesAdapter(
        private var dataset: MutableList<String>,
        private val context: Context?,
        private var listener: OnItemClickListener
) :
    RecyclerView.Adapter<CreatedClassesAdapter.ViewHolder>()  {

    private var database: DatabaseReference = Firebase.database.reference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.each_createdclass_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var key = getDataset().get(position)
        database.child("channels").child(key).child("channel_name").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val className = snapshot.value?.toString()

                holder.textView.text= className
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

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
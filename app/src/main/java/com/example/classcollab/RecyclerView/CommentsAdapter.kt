package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*
Comments Adapter acts as an adapter for those recycler views that have been used for all comment sections in the app
 */
class CommentsAdapter(
        private val context: Context?,
        private var commentsIdList: MutableList<String>?
) :
        RecyclerView.Adapter<CommentsAdapter.ViewHolder>()
{
    private lateinit var database: DatabaseReference


//    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        database = Firebase.database.reference
        val commentId = commentsIdList!!.get(position)

        database.child("comments").child(commentId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    if(it.key.toString().equals("comment")){
                        holder.eachCommentTv.setText(it.value.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
//        println("comments = " + commentId)

    }

    override fun getItemCount(): Int {
        if(commentsIdList!!.size == 0)
            return 0
        return getCommentsIdList().size
    }

    fun getCommentsIdList(): MutableList<String> {
        return commentsIdList!!
    }

    fun setCommentsIdList(passedQS: MutableList<String>){

        commentsIdList = passedQS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eachCommentTv = view.findViewById<TextView>(R.id.each_comment_tv)
    }
}
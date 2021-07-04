package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.ArrayStringViewModel
import com.example.classcollab.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

class CommentsAdapter(
        private val context: Context?,
        private var commentsIdList: MutableList<String>
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
        val commentId = commentsIdList.get(position)
        holder.eachCommentTv.setText(commentId)
    }

    override fun getItemCount(): Int {
        if(commentsIdList.size == 0)
            return 0
        return getCommentsIdList().size
    }

    fun getCommentsIdList(): MutableList<String> {
        return commentsIdList
    }

    fun setCommentsIdList(passedQS: MutableList<String>){

        commentsIdList = passedQS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eachCommentTv = view.findViewById<TextView>(R.id.each_comment_tv)
    }
}
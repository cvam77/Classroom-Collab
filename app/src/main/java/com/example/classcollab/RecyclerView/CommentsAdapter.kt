package com.example.classcollab.RecyclerView

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.IndividualQuestionDirections
import com.example.classcollab.R
import com.example.classcollab.model.CommentModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

/*
Comments Adapter acts as an adapter for those recycler views that have been used for
 all comment sections in the app
 */
class CommentsAdapter(
        private val context: Context?,
        private var indivCommentList: MutableList<CommentModel>?,
        private var listener: CommentsAdapter.OnItemClickListener
) :
        RecyclerView.Adapter<CommentsAdapter.ViewHolder>()
{

    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var firstPositionMarked: Boolean = false // this variable used so that multiple comments are not bolded
//    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        println(indivCommentList)
        val view = LayoutInflater.from(context).inflate(R.layout.comment_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        database = Firebase.database.reference

        val indivComment: CommentModel = getIndivCommentsList().get(position)

//        println(indivComment)
        holder.commentTime.setText(indivComment.comment_time)
//        holder.commentPic.setImageURI(indivComment.image.toUri())
        holder.commenter.setText(indivComment.commenter)
        holder.eachCommentTv.setText(indivComment.actual_comment)

        val fileName = indivComment.image
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        val localfile = File.createTempFile("tempImage","jpg")
        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)

            //comment picture
            holder.commentPic.visibility=View.VISIBLE
            holder.commentPic.setImageBitmap(bitmap)
            holder.commentPic.setOnClickListener(View.OnClickListener {
                val action = IndividualQuestionDirections.actionIndividualQuestionToViewIndividualImage(fileName.toString())
                Navigation.findNavController(it).navigate(action)
            })
        }.addOnFailureListener{
            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
        }


//        println(commentId)
//        database.child("comments").child(commentId).addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val children = snapshot!!.children
//                children.forEach {
//
//                    println(it.key.toString())
//                    println()
//                    if(it.key.toString().equals("commenter")){
//                        holder.commenter.setText(it.value.toString())
//                    }
//                    if(it.key.toString().equals("actual_comment")){
//                        holder.eachCommentTv.setText(it.value.toString())
//                    }
//                    if(it.key.toString().equals("comment_time")){
//                        holder.commentTime.setText(it.value.toString())
//                    }
////                    if(it.key.toString().equals("image")){
////                        val fileName = it.value
////                        //comment time
////                        holder.commentTime.setText(fileName.toString())
////
////                        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
////
////                        val localfile = File.createTempFile("tempImage","jpg")
////                        storageReference.getFile(localfile).addOnSuccessListener {
////                            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
////
////                            //comment picture
////                            holder.commentPic.visibility=View.VISIBLE
////                            holder.commentPic.setImageBitmap(bitmap)
////                            holder.commentPic.setOnClickListener(View.OnClickListener {
////                                val action = IndividualQuestionDirections.actionIndividualQuestionToViewIndividualImage(fileName.toString())
////                                Navigation.findNavController(it).navigate(action)
////                            })
////                        }.addOnFailureListener{
////                            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
////                        }
////                    }
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
////                TODO("Not yet implemented")
//            }
//
//        })
//        println("comments = " + commentId)

//        if(position==0 && !firstPositionMarked){
//            holder.eachCommentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25f)
//            holder.eachCommentTv.setTypeface(Typeface.DEFAULT_BOLD)
//            firstPositionMarked = true
////            holder.commentPic.layoutParams.height = 350
////            holder.commentPic.layoutParams.width = 350
//
//        }

    }

    override fun getItemCount(): Int {
        if(indivCommentList!!.size == 0)
            return 0
        return getIndivCommentsList().size
    }

    fun getIndivCommentsList(): MutableList<CommentModel> {
        return indivCommentList!!
    }

    fun setIndivCommentsList(passedQS: MutableList<CommentModel>){

        indivCommentList = passedQS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val eachCommentTv = view.findViewById<TextView>(R.id.each_comment_tv)
        val commenter = view.findViewById<TextView>(R.id.commenter_tv)

        val commentTime = view.findViewById<TextView>(R.id.comment_time_tv)
        val commentPic = view.findViewById<ImageView>(R.id.iv_single_comment_pic)



        init{
            view.setOnClickListener(this)
        }



        override fun onClick(p0: View?) {
            val position:Int = absoluteAdapterPosition

            if(position != RecyclerView.NO_POSITION)
            {
                val string = getIndivCommentsList().get(position)
                listener.onItemClick(string)
            }

        }
    }

    interface OnItemClickListener{
        abstract fun onItemClick(position: CommentModel)
    }
}
package com.example.classcollab.RecyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.IndividualQuestionDirections
import com.example.classcollab.R
import com.example.classcollab.model.CommentModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

/*
Comments Adapter acts as an adapter for those recycler views that have been used for
 all comment sections in the app
 */
class CommentsAdapter(
    private val context: Context?,
    private var indivCommentList: MutableCollection<CommentModel>,
    private var listener: OnItemClickListener
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
//        database = Firebase.database.reference

        val indivComment: CommentModel = getIndivCommentsList().get(position)

        holder.commentTime.setText(indivComment.comment_time)
//        holder.commentPic.setImageURI(indivComment.image.toUri())
        holder.commenter.setText(indivComment.commenter)

        holder.eachCommentTv.setText(indivComment.actual_comment)

        if(indivComment.bitmap != null){
            holder.commentPic.visibility=View.VISIBLE
            holder.commentPic.setImageBitmap(indivComment.bitmap)

            holder.commentPic.setOnClickListener(View.OnClickListener {
                val action = IndividualQuestionDirections.actionIndividualQuestionToViewIndividualImage(indivComment.image.toString())
                Navigation.findNavController(it).navigate(action)
            })
        }
//        val fileName = indivComment.image
//        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
//
//        val localfile = File.createTempFile("tempImage","jpg")
//        storageReference.getFile(localfile).addOnSuccessListener {
//            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//
//            //comment picture
//            holder.commentPic.visibility=View.VISIBLE
//            holder.commentPic.setImageBitmap(bitmap)
//            holder.commentPic.setOnClickListener(View.OnClickListener {
//                val action = IndividualQuestionDirections.actionIndividualQuestionToViewIndividualImage(fileName.toString())
//                Navigation.findNavController(it).navigate(action)
//            })
//        }.addOnFailureListener{
////                Toast.makeText(context,"magna cum laude", Toast.LENGTH_SHORT).show()
//        }

    }

    override fun getItemCount(): Int {
        if(indivCommentList!!.size == 0)
            return 0
        return getIndivCommentsList().size
    }

    fun getIndivCommentsList(): MutableList<CommentModel> {
        return indivCommentList!!.toMutableList()

    }

    fun setIndivCommentsList(passedQS: MutableCollection<CommentModel>){

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
                val string = getIndivCommentsList().elementAt(position)
                listener.onItemClick(string)
            }

        }
    }

    interface OnItemClickListener{
        abstract fun onItemClick(position: CommentModel)
    }
}
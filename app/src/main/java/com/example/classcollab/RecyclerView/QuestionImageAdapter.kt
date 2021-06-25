package com.example.classcollab.RecyclerView

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class QuestionImageAdapter(
    private val context: Context?,
    private var questionSet: MutableList<String>
) :
        RecyclerView.Adapter<QuestionImageAdapter.ViewHolder>()
{
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.question_set_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var questionId = getQuestionSet().get(position)
        database = Firebase.database.reference

        database.child("questions").child(questionId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    if(it.key.equals("title")) {
                        holder.questionTitleTv.text = it.value.toString()
                    }
                    else if(it.key.equals("image")){
                        val imageChildren = it!!.children
                        imageChildren.forEach {
                            holder.textViewTime.text = it.key
                            holder.textViewEmail.text = it.value.toString()

                            val fileName = it.key
                            storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

                            val localfile = File.createTempFile("tempImage","jpg")
                            storageReference.getFile(localfile).addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                holder.imageViewQuestion.setImageBitmap(bitmap)
                            }.addOnFailureListener{
                                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    println("key = " + it.key )
                    println("value = " + it.value )
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })

        holder.textViewEmail.text = "abc@yahoo.com"
        holder.textViewTime.text = questionId
        holder.imageViewQuestion.setImageURI(null)
    }

    override fun getItemCount(): Int {
        if(questionSet.size == 0)
            return 0
        return getQuestionSet().size
    }

    fun getQuestionSet(): MutableList<String> {
        return questionSet
    }

    fun setQuestionSet(passedQS: MutableList<String>){

        questionSet = passedQS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTitleTv = view.findViewById<TextView>(R.id.question_title_tv)
        val textViewEmail = view.findViewById<TextView>(R.id.username_tv)
        val textViewTime = view.findViewById<TextView>(R.id.timestamp_tv)
        val imageViewQuestion = view.findViewById<ImageView>(R.id.question_iv)
    }
}
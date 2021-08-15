package com.example.classcollab.RecyclerView

import android.content.Context
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

/*
Usually if you go to the second or third level deep inside the app, the screen is divided into two parts:-
(i). top part is folder part
(ii). bottom part is question part. The bottom part (the question part) is again divided into (1) top part (image part) and (2) bottom part (comments part)

To visualize, the screen looks like this -

(Top Part) -> Folder Lists
(Bottom Part) -> Question Lists -> (Question Title, Question Uploaded Time, Question Uploader Email, Question Image)
                 (Comments)

Question Image Adapter deals with the Bottom Part of the screen:-
(1) top part (image part):- First, it takes the question id, which is the key of the questionSet (Hashmap) passed to QuestionImageAdapter from other classes.
It uses this question id to fetch and show the top parts (question title, image part, question uploader email, question uploaded time) of the question.
(2) bottom part (comments part):- Second, it takes the value of the questionSet (Hashmap) passed to QuestionImageAdapter from other classes. This value
of hashmap is basically the list of comments for a particular question. Question image adapter just takes this comment lists and passes them to CommentsAdapter
to show the comments for each question.
 */
class QuestionImageAdapter(
        private val context: Context?,
        private var questionSet: HashMap<String, MutableList<String>>
) :
        RecyclerView.Adapter<QuestionImageAdapter.ViewHolder>() {
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    lateinit var commentsAdapter: CommentsAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.question_set_layout,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println("map is " + getQuestionSet())

        var questionId = getQuestionSet().keys.elementAt(position)

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


                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })

        holder.textViewEmail.text = "abc@yahoo.com"
        holder.textViewTime.text = questionId
        holder.imageViewQuestion.setImageURI(null)



        holder.doneButton.setOnClickListener(View.OnClickListener {
            val comment = holder.addCommentEt.text.toString()
            when{
                TextUtils.isEmpty(comment.trim { it <= ' '}) ->{
                    Toast.makeText(context,
                            "No comments!",
                            Toast.LENGTH_SHORT).show()
                }

                else -> {
//                    val formatter = SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.getDefault())
//                    val now = Date()
//                    val commentTime = formatter.format(now)

//                    getDate(82233213123L, "dd/MM/yyyy hh:mm:ss.SSS")

//                    public static String getDate(long milliSeconds, String dateFormat)
//                    {
//                        // Create a DateFormatter object for displaying date in specified format.
//                        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//
//                        // Create a calendar object that will convert the date and time value in milliseconds to date.
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(milliSeconds);
//                        return formatter.format(calendar.getTime());
//                    }
                    val curTime = System.currentTimeMillis().toString()
                    database.child("questions").child(questionId).child("comments").child(curTime).setValue("true")
                    val commentPath = database.child("comments").child(curTime)
                    commentPath.child("commenter").setValue(FirebaseAuth.getInstance().currentUser?.email)
                    commentPath.child("comment").setValue(comment)
                    holder.addCommentEt.setText("")
                }
            }
        })


        // comments part
        val commentLayoutManager = LinearLayoutManager(context)
        val commentsRv: RecyclerView = holder.commentsRv
//        val commentsList: MutableList<String> = prepareCommentArrayList(questionId)
        var emptyList = getQuestionSet().get(questionId)
//        commentsAdapter = CommentsAdapter(context,emptyList,this)
        commentsRv.layoutManager = commentLayoutManager
        commentsRv.itemAnimator = DefaultItemAnimator()
        commentsRv.adapter = commentsAdapter


    }

//    private fun prepareCommentArrayList(questionId: String): MutableList<String> {
//
//        database.child("questions").child(questionId).child("comments").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                TODO("Not yet implemented")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }

    override fun getItemCount(): Int {
        if(questionSet.size == 0)
            return 0
        return getQuestionSet().size
    }

    fun getQuestionSet(): HashMap<String, MutableList<String>> {
        return questionSet
    }

    fun setQuestionSet(passedQS: HashMap<String, MutableList<String>>){
        questionSet = passedQS
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionTitleTv = view.findViewById<TextView>(R.id.question_title_tv)
        val textViewEmail = view.findViewById<TextView>(R.id.username_tv)
        val textViewTime = view.findViewById<TextView>(R.id.timestamp_tv)
        val imageViewQuestion = view.findViewById<ImageView>(R.id.question_iv)

        val commentsRv = view.findViewById<RecyclerView>(R.id.comments_rv)
        val addCommentEt = view.findViewById<EditText>(R.id.add_comment_et)
        val doneButton = view.findViewById<Button>(R.id.done_btn)
    }

}
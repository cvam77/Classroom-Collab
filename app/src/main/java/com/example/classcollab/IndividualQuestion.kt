package com.example.classcollab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.CommentsAdapter
import com.example.classcollab.databinding.FragmentIndividualQuestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class IndividualQuestion : Fragment(), CommentsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentIndividualQuestionBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    val arguments: IndividualQuestionArgs by navArgs()
    var ImageUri : Uri? = null
    private lateinit var storageReference: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_individual_question, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIndividualQuestionBinding.bind(view)
        database = Firebase.database.reference
        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)

        var emptyList = mutableListOf<String>()
        commentsAdapter = CommentsAdapter(context,emptyList, this)
        val recyclerView: RecyclerView = binding.rvIndivQuestions
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = commentsAdapter

        viewModel.indivCommentIdsVM.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            commentsAdapter.setCommentsIdList(it)
        })



        binding.moreOptionsPopup.setOnClickListener {
            val popup = PopupMenu(context, binding.moreOptionsPopup)
            popup.inflate(R.menu.add_question_menu)
            popup.setOnMenuItemClickListener {
                popUpClicked(it.title as String)
                true
            }
            popup.show()
        }

        binding.btnSend.setOnClickListener(View.OnClickListener {
            binding.ivPhotoComment.setImageURI(null)
            binding.ivPhotoComment.visibility = View.GONE
            AddCommentToFirebase()
        })

        PrepareCommentIdList()

        var scrollView: ScrollView = binding.scrollLayout
        scrollView.postDelayed(
            {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }, 1000
        )
    }

    private fun PrepareCommentIdList() {
        database.child("questions").child(arguments.questionId).child("comments").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.indivCommentIds.clear()
                val children = snapshot!!.children

                children.forEach{
                    viewModel.indivCommentIds.add(it.key.toString())
                }
                viewModel.indivCommentIdsVM.value = viewModel.indivCommentIds
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //Adding comment to firebase - both to questions and comments branches
    fun AddCommentToFirebase() {
        val enteredComment: String = binding.etType.text.toString().trim{it <= ' '}
        //this line is executed only if at least one of edittext or imageuri is not null
        if(!(TextUtils.isEmpty(enteredComment) && ImageUri == null)){

            val path = database.child("questions").child(arguments.questionId).child("comments")
            val actualCommentKey = path.push().key.toString()

            path.child(actualCommentKey).setValue("true")

            val currentUser = FirebaseAuth.getInstance().currentUser?.email

            val commentPath = database.child("comments").child(actualCommentKey)
            commentPath.child("commenter").setValue(currentUser)
            if(ImageUri != null)
            {
                val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
                val now = Date()
                var fileName = formatter.format(now)
                storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

                storageReference.putFile(ImageUri!!).addOnSuccessListener {
                    commentPath.child("image").setValue(fileName)

                }.addOnFailureListener{
                    Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
                }

                ImageUri = null
            }

            if(!TextUtils.isEmpty(enteredComment) )
            {
                commentPath.child("actual_comment").setValue(enteredComment)
                binding.etType.setText("")

                //Making keyboard disappear
                val mgr: InputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                mgr.hideSoftInputFromWindow(binding.etType.getWindowToken(), 0)

            }
        }
    }

    //If you click on "..." textview -> and then, one of the popup options, this method is triggered
    fun popUpClicked(option: String){

        if(option.equals("Upload From Gallery")){
            UploadFromGallery()
        }
    }

    //Upload an image from the gallery
    private fun UploadFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        intent.type = "images/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK)
        {
            ImageUri = data?.data!!
            setCommentImageView(ImageUri.toString())
        }
    }

    //set the image selected from gallery to comment imageview
    private fun setCommentImageView(imgUri: String){
        val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
        val now = Date()
        var fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        binding.ivPhotoComment.visibility = View.VISIBLE
        binding.ivPhotoComment.setImageURI(ImageUri)

    }

    override fun onItemClick(position: String) {
//        TODO("Not yet implemented")
    }

//    //Upload the image selected from gallery to firebase
//    private fun UploadToFirebase(){
//        val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
//        val now = Date()
//        var fileName = formatter.format(now)
//        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
//
//        if(ImageUri != null)
//        {
//            storageReference.putFile(ImageUri!!).addOnSuccessListener {
//                Toast.makeText(context,"Successfully uploaded", Toast.LENGTH_SHORT).show()
//
////            val path = database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child("actual_question")
//
//                val path = database.child("questions").child(arguments.questionId).child("comments")
//                val actualCommentKey = path.push().key
//
//                val currentUser = FirebaseAuth.getInstance().currentUser?.email
//                if (actualCommentKey != null) {
//                    path.child(actualCommentKey).setValue("true")
////                val map = mapOf(fileName to currentUser)
////                database.child("questions").child(actualCommentKey).child("image").setValue(map)
////
//                    database.child("comments").child(actualCommentKey).child("image").setValue(fileName)
//                    database.child("comments").child(actualCommentKey).child("commenter").setValue(currentUser)
//
//
////                setImageView(fileName)
//                }
//
//            }.addOnFailureListener{
//                Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }

}
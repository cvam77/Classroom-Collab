package com.example.classcollab

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.QuestionImageAdapter
import com.example.classcollab.databinding.FragmentIndividualQuestionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class IndividualQuestion : Fragment() {

    private lateinit var binding: FragmentIndividualQuestionBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var questionImageAdapter: QuestionImageAdapter
    val arguments: IndividualQuestionArgs by navArgs()
    lateinit var ImageUri : Uri
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIndividualQuestionBinding.bind(view)
        database = Firebase.database.reference
        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)

        val recyclerView: RecyclerView = binding.rvIndivQuestions
        var emptyList = mutableListOf<String>()

        binding.moreOptionsPopup.setOnClickListener({
            val popup = PopupMenu(context,binding.moreOptionsPopup)
            popup.inflate(R.menu.add_question_menu)
            popup.setOnMenuItemClickListener {
                popUpClicked(it.title as String)
                true
            }
            popup.show()
        })
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

    //Upload the image selected from gallery to firebase
    private fun UploadToFirebase(imgUri: String){
        val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
        val now = Date()
        var fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        if(ImageUri != null)
        {
            storageReference.putFile(ImageUri).addOnSuccessListener {
                Toast.makeText(context,"Successfully uploaded", Toast.LENGTH_SHORT).show()

//            val path = database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child("actual_question")
                val path = database.child("questions").child(arguments.questionId).child("comments")
                val actualCommentKey = path.push().key

                val currentUser = FirebaseAuth.getInstance().currentUser?.email
                if (actualCommentKey != null) {
                    path.child(actualCommentKey).setValue("true")
//                val map = mapOf(fileName to currentUser)
//                database.child("questions").child(actualCommentKey).child("image").setValue(map)
//
                    database.child("comments").child(actualCommentKey).child("image").setValue(fileName)
                    database.child("comments").child(actualCommentKey).child("commenter").setValue(currentUser)


//                setImageView(fileName)
                }

            }.addOnFailureListener{
                Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
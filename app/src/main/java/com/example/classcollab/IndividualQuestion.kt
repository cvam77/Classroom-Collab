package com.example.classcollab

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.classcollab.RecyclerView.CommentsAdapter
import com.example.classcollab.databinding.FragmentIndividualQuestionBinding
import com.example.classcollab.helper.SetUpIndivList
import com.example.classcollab.model.CommentModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class IndividualQuestion : Fragment(), CommentsAdapter.OnItemClickListener {

    private lateinit var binding: FragmentIndividualQuestionBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var commentsAdapter: CommentsAdapter
    val arguments: IndividualQuestionArgs by navArgs()
    var ImageUri : Uri? = null
    var ImageByteArray: ByteArray? = null
    private lateinit var storageReference: StorageReference
    var commentIdsListFirebase = mutableListOf<String>()
    var indivCommentList = mutableListOf<CommentModel>()
    private var emptySymbolString: String = ""
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

        commentsAdapter = CommentsAdapter(context,viewModel.indivComments.values, this)
        val recyclerView: RecyclerView = binding.rvIndivQuestions
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true

        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = commentsAdapter

        viewModel.indivCommentsVM.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            commentsAdapter.setIndivCommentsList(it.values)
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

            binding.ivPhotoComment.visibility = View.GONE
            AddCommentToFirebase()
//            recyclerView.scrollToPosition(commentsAdapter.itemCount-1)

            //scroll to bottom
            var scrollView: ScrollView = binding.scrollLayout
            scrollView.postDelayed(
                {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }, 1000
            )
        })

        //scroll to bottom
//        var scrollView: ScrollView = binding.scrollLayout
//        scrollView.postDelayed(
//            {
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN)
//            }, 1000
//        )

        PrepareCommentIdList()

//        viewModel.indivComments = SetUpIndivList(viewModel.indivComments as MutableList<CommentModel>).getFileFromFirebase()
//        getFileFromFirebase(viewModel.indivComments)
//        commentsAdapter.notifyDataSetChanged()
    }

    //bulk download images
    fun getSingleImageFromFirebase(commentId: String, fileName: String){
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
        val localfile = File.createTempFile("tempImage","jpg")

        storageReference.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            viewModel.indivComments.get(commentId)?.bitmap=bitmap
            viewModel.indivCommentsVM.value = viewModel.indivComments
            viewModel.indivComments
            commentsAdapter.notifyDataSetChanged()
        }


    }

    //bulk download images
    fun getFileFromFirebase(indivList: LinkedHashMap<String, CommentModel>){
        for (comment in indivList.values){
            val fileName = comment.image

            val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
            val localfile = File.createTempFile("tempImage","jpg")

            storageReference.getFile(localfile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                comment.bitmap = bitmap
                viewModel.indivCommentsVM.value = viewModel.indivComments
            }
        }


    }

    private fun PrepareCommentIdList() {
//        commentIdsListFirebase.clear()
        database.child("questions").child(arguments.questionId).child("comments").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                    //Commenting viewmodel part
//                viewModel.indivCommentIds.clear()
//                val children = snapshot!!.children
//
//                children.forEach{
//                    viewModel.indivCommentIds.add(it.key.toString())
//                }
//                viewModel.indivCommentIdsVM.value = viewModel.indivCommentIds

                //Applying non viewmodel part
                val children = snapshot!!.children

                children.forEach{
//                    if(!commentIdsListFirebase.contains(it.key.toString())){
//
//                        commentIdsListFirebase.add(it.key.toString())
//                        GetActualComment(it.key.toString())
//                    }

                    if(!viewModel.indivComments.containsKey(it.key.toString())){

                        viewModel.indivComments[it.key.toString()] = CommentModel()
                        GetActualComment(it.key.toString())
                    }
//                    commentIdsListFirebase

                    //notes = problem with viewmodel

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    //Get comment using the passed commentId
    private fun GetActualComment(commentId: String) {


        database.child("comments").child(commentId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

//                if(viewModel.indivComments.contains())
//                var eachComment = CommentModel()
//                eachComment.commentId = commentId

                val children = snapshot!!.children

                children.forEach {


                    if(it.key.toString().equals("commenter")){
                        viewModel.indivComments.get(commentId)?.commenter=it.value.toString()
                    }
                    if(it.key.toString().equals("actual_comment")){
                        viewModel.indivComments.get(commentId)?.actual_comment=it.value.toString()
                    }
                    if(it.key.toString().equals("comment_time")){
                        viewModel.indivComments.get(commentId)?.comment_time=it.value.toString()
                    }
                    if(it.key.toString().equals("image")){
                        viewModel.indivComments.get(commentId)?.image=it.value.toString()
                        getSingleImageFromFirebase(commentId,it.value.toString())
                    }

                }
//                commentsAdapter.notifyDataSetChanged()

//                indivCommentList.add((eachComment))
//                commentsAdapter.notifyDataSetChanged()
//                viewModel.indivComments.add(eachComment)
//                viewModel.indivCommentsVM.value = viewModel.indivComments
//                viewModel.indivCommentsVM.value = viewModel.indivComments
                commentsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })

//        viewModel.indivComments.add(eachComment)
//        indivCommentList.add(eachComment)
//        commentsAdapter.notifyDataSetChanged()
    }

    //this function will iterate the indivCommentList ( a list of commentmodel objects)
    //and add imageUri to the object with the passedCommentId
//    private fun AddImageToComment(passedCommentId: String, imageUri: String) {
//        indivCommentList.forEach{indivComment->
//            if(indivComment.commentId == passedCommentId){
//                indivComment.image = imageUri
//                commentsAdapter.notifyDataSetChanged()
//            }
//        }
//    }

    //Adding comment to firebase - both to questions and comments branches
    fun AddCommentToFirebase() {
        val enteredComment: String = binding.etType.text.toString().trim{it <= ' '}
        //this line is executed only if at least one of edittext or imageuri or imageByteArray is not null
        if(!(TextUtils.isEmpty(enteredComment) && ImageUri == null && ImageByteArray == null)){

            val path = database.child("questions").child(arguments.questionId).child("comments")
            val actualCommentKey = path.push().key.toString()

            path.child(actualCommentKey).setValue("true")

            val currentUser = FirebaseAuth.getInstance().currentUser?.email

            val commentPath = database.child("comments").child(actualCommentKey)
            commentPath.child("commenter").setValue(currentUser)

            val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
            val now = Date()
            var fileName = formatter.format(now)

            //Adding comment time
            commentPath.child("comment_time").setValue(fileName)

            storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

            if(ImageUri != null)
            {
                storageReference.putFile(ImageUri!!).addOnSuccessListener {
                    commentPath.child("image").setValue(fileName)


                }.addOnFailureListener{
                    Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
                }


            }

            else if(ImageByteArray != null)
            {
                val uploadTask: UploadTask = storageReference.putBytes(ImageByteArray!!)

                uploadTask
                    .addOnFailureListener {
                        Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener { snapshot ->
                        commentPath.child("image").setValue(fileName)
//                        val downloadURL: Task<Uri>? = snapshot.result?.storage?.downloadUrl
////                        while (!downloadURL?.isComplete!!) {
////                            //don't go to next line
////                        }
////                        val uri: Uri? = downloadURL.result
////                        setCommentImageViewFromCameraImage(URL(uri.toString()))
                    }
//                ImageByteArray = null
            }
            else{
                commentPath.child("image").setValue(emptySymbolString)
            }


            if(!TextUtils.isEmpty(enteredComment) )
            {
                commentPath.child("actual_comment").setValue(enteredComment)
            }else{
                commentPath.child("actual_comment").setValue(emptySymbolString)
            }

            binding.etType.setText("")

            //Making keyboard disappear
            val mgr: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            mgr.hideSoftInputFromWindow(binding.etType.getWindowToken(), 0)
        }

        binding.ivPhotoComment.setImageURI(null)
        ImageUri = null
        ImageByteArray = null
    }

    //If you click on "..." textview -> and then, one of the popup options, this method is triggered
    fun popUpClicked(option: String){

        if(option.equals("Upload From Gallery")){
            UploadFromGallery()
        }
        else if(option.equals("Take Picture and Upload")){
            TakePicture()
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if(requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
//        {
//
//        }
//    }

    //Upload an image from the gallery
    private fun UploadFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        intent.type = "images/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    //OR//

    //take picture from camera
    private fun TakePicture(){
        if (context?.let { ActivityCompat.checkSelfPermission(it,android.Manifest.permission.CAMERA) } != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(android.Manifest.permission.CAMERA),200)
        } else{
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i,201)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK)
        {
            ImageUri = data?.data!!
            setCommentImageView(ImageUri.toString())
        } else if(requestCode == 201 && resultCode == Activity.RESULT_OK){

            //call a method that can convert captured image data into byte array
            onCapureImageResult(data)

        }
    }

    //When a user selects "Take Picture and Upload" from the pop up menu, a camera opens -> user
    // clicks picture -> then hits ok -> gets data in onActivityResult -> this 'data' is passed
    //to the below method to be converted into byte array which is uploaded to firebase storage
    private fun onCapureImageResult(data: Intent?) {
        val thumbnail = data!!.extras!!["data"] as Bitmap?
        val bytes = ByteArrayOutputStream()
        thumbnail!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val bb = bytes.toByteArray()
        ImageByteArray = bb
        //call this method so that the byte array 'bb' is uploaded to firebase storage, which returns
        //a url, which is used to show the captured image in comment imageview 'ivPhotoComment'
        temporarilyStoreImageAtFirebaseStorage(bb)
    }

    //this method uploads the byte array 'bb' to firebase storage, which returns
    //a url, which is used to show the captured image in comment imageview 'ivPhotoComment'
    fun temporarilyStoreImageAtFirebaseStorage(bb: ByteArray) {
//        val s = String(bb, charset("UTF-8"))
//        val uri = Uri.parse(s)
//        ImageUri = uri

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        storageReference = FirebaseStorage.getInstance().getReference("images/$userId")

        val uploadTask: UploadTask = storageReference.putBytes(bb)

        uploadTask
            .addOnFailureListener {
                Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener{snapshot ->
                
                val downloadURL: Task<Uri>? = snapshot.result?.storage?.downloadUrl
                while(!downloadURL?.isComplete!!)
                {
                    //don't go to next line
                }
                val uri: Uri? = downloadURL.result
                setCommentImageViewFromCameraImage(URL(uri.toString()))
            }

    }



    //set the image selected from gallery to comment imageview
    private fun setCommentImageView(imgUri: String){
//        val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
//        val now = Date()
//        var fileName = formatter.format(now)
//        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        binding.ivPhotoComment.visibility = View.VISIBLE
        binding.ivPhotoComment.setImageURI(ImageUri)

    }

    //OR//

    //set the image captured from camera to comment imageview
    private fun setCommentImageViewFromCameraImage(imgUrl: URL){
        binding.ivPhotoComment.visibility = View.VISIBLE
        Glide.with(context).load(imgUrl).into(binding.ivPhotoComment)
    }

    override fun onItemClick(position: CommentModel) {
//        TODO("Not yet implemented")
    }



}
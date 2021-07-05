package com.example.classcollab

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.LevelAdapter
import com.example.classcollab.RecyclerView.QuestionImageAdapter
import com.example.classcollab.databinding.FragmentAssignmentFieldBinding
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
import kotlin.collections.HashMap

class AssignmentField : Fragment(), LevelAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAssignmentFieldBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var levelAdapter: LevelAdapter
    private lateinit var questionImageAdapter: QuestionImageAdapter
    val arguments: AssignmentFieldArgs by navArgs()
    lateinit var ImageUri : Uri
    private lateinit var storageReference: StorageReference
    private lateinit var questionTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_field, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAssignmentFieldBinding.bind(view)
        database = Firebase.database.reference

        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)


        val recyclerView: RecyclerView = binding.rvCreatedClasses

        var emptyList = mutableListOf<String>()
        levelAdapter = LevelAdapter(emptyList,context,this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = levelAdapter

        viewModel.levelStringsVM.observe(viewLifecycleOwner, Observer {
            levelAdapter.setDataset(it)
        })

        prepareLevelString()

        if(arguments.assignmentFieldType.equals("misc"))
        {
            val questionlayoutManager = LinearLayoutManager(context)
            val questionRecyclerView: RecyclerView = binding.rvIndividualQuestions
            var emptyListList: HashMap<String, MutableList<String>> = HashMap()
            emptyListList.put("questionId", mutableListOf())
            questionImageAdapter = QuestionImageAdapter(context,emptyListList)
            questionRecyclerView.layoutManager = questionlayoutManager
            questionRecyclerView.itemAnimator = DefaultItemAnimator()
            questionRecyclerView.adapter = questionImageAdapter

            viewModel.questionIDsMapVM.observe(viewLifecycleOwner, Observer {
                questionImageAdapter.setQuestionSet(it)
            })

            prepareQuestionIdList()
        }

        binding.addFolder.setOnClickListener(View.OnClickListener {
            OpenDialogBox("section","")
        })

        binding.addQuestion.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(context,binding.addQuestion)
            popup.inflate(R.menu.add_question_menu)
            popup.setOnMenuItemClickListener {
                popUpClicked(it.title as String)
                true
            }
            popup.show()
        })
    }

    private fun prepareCommentsIdList(questionKey: String) {
//        TODO("Not yet implemented")
        database.child("questions").child(questionKey).child("comments").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.questionIDsMap.put(questionKey, mutableListOf())
                val children = snapshot!!.children
                children.forEach {
                    if(viewModel.questionIDsMap.containsKey(questionKey)){
                        viewModel.questionIDsMap.get(questionKey)!!.add(it.key.toString())
                        viewModel.questionIDsMapVM.value = viewModel.questionIDsMap
                    }
                    else{
                        viewModel.questionIDsMap.put(questionKey, mutableListOf(it.key.toString()))
                        viewModel.questionIDsMapVM.value = viewModel.questionIDsMap

                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun prepareQuestionIdList() {
        database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child("actual_question").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.questionIDsMap.clear()
                val children = snapshot!!.children
                children.forEach {
                    val questionKey = it.key.toString()
                    viewModel.questionIDsMap.put(questionKey, mutableListOf())
                    viewModel.questionIDsMapVM.value = viewModel.questionIDsMap
                    prepareCommentsIdList(questionKey)
//                    if(viewModel.questionIDsMap.containsKey(questionKey)){
//                        viewModel.questionIDsMap.get("questionId")!!.add(it.key.toString())
//                        viewModel.questionIDsMapVM.value = viewModel.questionIDsMap
//                    }
//                    else{
//                        viewModel.questionIDsMap.put("questionId", mutableListOf(it.key.toString()))
//                        viewModel.questionIDsMapVM.value = viewModel.questionIDsMap
//                    }


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
//        database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                viewModel.levelStrings.clear()
//                val children = snapshot!!.children
//                children.forEach {
//                    if(!(it.key.equals("actual_question")))
//                    {
//                        viewModel.levelStrings.add(it.key.toString())
//                        viewModel.levelStringsVM.value = viewModel.levelStrings
//                    }
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
    }

    fun OpenDialogBox(dialogTitle: String, dialogOption: String){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.enter_name_et,null)
        val edittext = dialogLayout.findViewById<EditText>(R.id.enter_name_edittext)

        if(dialogTitle.equals("section")){
            with(builder){
                setTitle("Enter the new $dialogTitle name")
                setPositiveButton("OK"){dialog, which ->
                    val thirdLevel = edittext.text.toString()
                    database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child(thirdLevel).setValue("nothing")

                }
                setNegativeButton("Cancel"){dialog,which->}
                setView(dialogLayout)
                show()
            }
        }
        else if(dialogTitle.equals("question")){
            with(builder){
                setTitle("Enter a $dialogTitle topic before proceeding!")
                setPositiveButton("OK"){dialog, which ->

                    questionTitle = edittext.text.toString()
                    when{
                        TextUtils.isEmpty(questionTitle.trim { it <= ' '}) ->{
                            Toast.makeText(context,
                                    "Enter a non empty name",
                                    Toast.LENGTH_SHORT).show()
                        }

                        else -> {
                            if(dialogOption.equals("Upload From Gallery")){
                                UploadFromGallery()
                            }
                        }
                    }
                }
                setNegativeButton("Cancel"){dialog,which->}
                setView(dialogLayout)
                show()
            }
        }

    }


    fun popUpClicked(option: String){

        if(option.equals("Upload From Gallery")){
            OpenDialogBox("question","Upload From Gallery")
        }
    }

    private fun UploadFromGallery() {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        intent.type = "images/*"
//        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,100)
    }

    private fun UploadToFirebase(imgUri: String){
        val formatter = SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(ImageUri).addOnSuccessListener {
            Toast.makeText(context,"Successfully uploaded", Toast.LENGTH_SHORT).show()

            val path = database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child("actual_question")
            val actualQuestionKey = path.push().key

            val currentUser = FirebaseAuth.getInstance().currentUser?.email
            if (actualQuestionKey != null) {
                path.child(actualQuestionKey).setValue("true")
                val map = mapOf(fileName to currentUser)
                database.child("questions").child(actualQuestionKey).child("image").setValue(map)
                database.child("questions").child(actualQuestionKey).child("title").setValue(questionTitle)

//                setImageView(fileName)
            }

        }.addOnFailureListener{
            Toast.makeText(context,"Failed!", Toast.LENGTH_SHORT).show()
        }

    }

//    private fun setImageView(fileName: String) {
//        val localfile = File.createTempFile("tempImage","jpg")
//        storageReference.getFile(localfile).addOnSuccessListener {
//            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//            binding.testIv.setImageBitmap(bitmap)
//        }.addOnFailureListener{
//            Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK)
        {
            ImageUri = data?.data!!
            UploadToFirebase(ImageUri.toString())
        }
    }

    fun prepareLevelString(){
        database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.levelStrings.clear()
                val children = snapshot!!.children
                children.forEach {
                    if(!(it.key.equals("actual_question")))
                    {
                        viewModel.levelStrings.add(it.key.toString())
                        viewModel.levelStringsVM.value = viewModel.levelStrings
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: String) {
//        TODO("Not yet implemented")
        val action = AssignmentFieldDirections.actionAssignmentFieldToThirdLevel(arguments.classId,arguments.assignmentFieldType,position)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }


}
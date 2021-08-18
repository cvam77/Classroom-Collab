package com.example.classcollab

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.LevelAdapter
import com.example.classcollab.databinding.FragmentIndividualClassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//This fragment just shows an individual class
class IndividualClass : Fragment(), LevelAdapter.OnItemClickListener {

    private lateinit var binding: FragmentIndividualClassBinding
    val arguments: IndividualClassArgs by navArgs()
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var levelAdapter: LevelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_individual_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference

        binding = FragmentIndividualClassBinding.bind(view)

        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)

        val recyclerView: RecyclerView = binding.rvCreatedClasses
        var emptyList = mutableListOf<String>()

        levelAdapter = LevelAdapter(emptyList,context,this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = levelAdapter

        viewModel.questionTopicVM.observe(viewLifecycleOwner, Observer {
            levelAdapter.setDataset(it)
        })

        binding.addFolder.setOnClickListener(View.OnClickListener {
            OpenDialogBox()
        })

        prepareLevelString()

        val classId = "Class Id: " + arguments.classId
        binding.classIdTv.text = classId

        database.child("channels").child(arguments.classId).child("channel_name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val className = snapshot.value?.toString()
                binding.classNameTv.text = className
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    //If you click "add a new folder" button, this method opens up a dialog box
    fun OpenDialogBox(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.enter_name_et,null)
        val edittext = dialogLayout.findViewById<EditText>(R.id.enter_name_edittext)

        with(builder){
            setTitle("Enter the new question title")
            setPositiveButton("OK"){dialog, which ->
                val uid = FirebaseAuth.getInstance().uid
                val questionTitleEntered = edittext.text.toString()
                val key = database.child("channels").child(arguments.classId).child("questions").push().key
                database.child("channels").child(arguments.classId).child("questions").child(key!!).setValue(questionTitleEntered)
                database.child("questions").child(key).child("question_title").setValue(questionTitleEntered)
                database.child("questions").child(key).child("questioner").setValue(uid)
            }
            setNegativeButton("Cancel"){dialog,which->}
            setView(dialogLayout)
            show()
        }
    }

    //Prepare a list of question titles here (...to be implemented here)
    fun prepareLevelString(){
        database.child("channels").child(arguments.classId).child("questions").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.questionTopic.clear()
                val children = snapshot!!.children
                children.forEach {

                    viewModel.questionTopic.add(it.key.toString())
                    viewModel.questionTopicVM.value = viewModel.questionTopic
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: String) {
//        TODO("Not yet implemented")
//        val action = IndividualClassDirections.actionIndividualClassToAssignmentField(arguments.classId,position)
        val action = IndividualClassDirections.actionIndividualClassToIndividualQuestion(position)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }


//    private fun CreateNewTv(text: String) {
//        database.child("channels").child(arguments.classId).child("channel_section").push().setValue(text)
//
//        val tv = TextView(context)
//        tv.textSize = 25f
//        tv.text = text
//        tv.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//        tv.gravity=Gravity.CENTER
//        tv.setTextColor(Color.parseColor("#000000"))
//        tv.setBackgroundColor(Color.parseColor("#FF018786"))
//        val space = 20
//        tv.setPadding(space, space, space, space)
//        (tv.layoutParams as LinearLayout.LayoutParams).setMargins(space, space, space, space)
////        <TextView
////        android:layout_width="match_parent"
////        android:layout_height="wrap_content"
////        android:gravity="center"
////        android:text="Miscellaneous"
////        android:textSize="25sp"
////        android:textColor="@color/black"
////        android:id="@+id/misc_tv"
////        android:background="@color/teal_700"
////        android:padding="10dp"
////        android:layout_margin="10dp"
////        />
//        binding.sectionListLl.addView(tv)
//    }
}
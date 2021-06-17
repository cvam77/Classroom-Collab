package com.example.classcollab

import android.app.AlertDialog
import android.os.Bundle
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
import com.example.classcollab.databinding.FragmentAssignmentFieldBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AssignmentField : Fragment(), LevelAdapter.OnItemClickListener {

    private lateinit var binding: FragmentAssignmentFieldBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var levelAdapter: LevelAdapter
    val arguments: AssignmentFieldArgs by navArgs()

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

        binding.addFolder.setOnClickListener(View.OnClickListener {
            OpenDialogBox()
        })

        binding.addQuestion.setOnClickListener(View.OnClickListener {
            val popup = PopupMenu(context,binding.addQuestion)
            popup.inflate(R.menu.add_question_menu)
            popup.setOnMenuItemClickListener {
                Toast.makeText(context,"Item: " + it.title, Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        })
    }

    fun OpenDialogBox(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.enter_name_et,null)
        val edittext = dialogLayout.findViewById<EditText>(R.id.enter_name_edittext)

        with(builder){
            setTitle("Enter the new section name")
            setPositiveButton("OK"){dialog, which ->
                val thirdLevel = edittext.text.toString()
                database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).child(thirdLevel).setValue("nothing")

            }
            setNegativeButton("Cancel"){dialog,which->}
            setView(dialogLayout)
            show()
        }
    }

    fun prepareLevelString(){
        database.child("channels").child(arguments.classId).child(arguments.assignmentFieldType).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.levelStrings.clear()
                val children = snapshot!!.children
                children.forEach {
                    viewModel.levelStrings.add(it.key.toString())
                    viewModel.levelStringsVM.value = viewModel.levelStrings
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
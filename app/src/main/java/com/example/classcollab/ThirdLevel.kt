package com.example.classcollab

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.LevelAdapter
import com.example.classcollab.databinding.FragmentAssignmentFieldBinding
import com.example.classcollab.databinding.FragmentThirdLevelBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/*
This fragment just shows the data that's in the third level of the firebase.
    Level starts "After" class id. So, for example,
    for a particular class with a unique id, "book" is level 1, "chapters" of the book is level 2, "question numbers" of the chapter
    is level 3. So, thirdlevelStrings just holds any string in the third level of the firebase.
    It is done this way, so that same code can be used for different sections of a class: tests, assignments, book, Misc. All of these sections have
    their own levels that start after the class id.
*/

class ThirdLevel : Fragment(), LevelAdapter.OnItemClickListener {

    private lateinit var binding: FragmentThirdLevelBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel
    private lateinit var levelAdapter: LevelAdapter
    val arguments: ThirdLevelArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third_level, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentThirdLevelBinding.bind(view)
        database = Firebase.database.reference

        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)


        val recyclerView: RecyclerView = binding.rvCreatedClasses
        var emptyList = mutableListOf<String>()

        levelAdapter = LevelAdapter(emptyList,context,this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = levelAdapter

        viewModel.thirdLevelStringsVM.observe(viewLifecycleOwner, Observer {
            levelAdapter.setDataset(it)
        })

        prepareLevelString()

        binding.addFolder.setOnClickListener(View.OnClickListener {
            OpenDialogBox()
        })
    }

    //If you click on either "add a new folder", you will get a dialogBox opened in this method
    fun OpenDialogBox(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.enter_name_et,null)
        val edittext = dialogLayout.findViewById<EditText>(R.id.enter_name_edittext)

        with(builder){
            setTitle("Enter the new section name")
            setPositiveButton("OK"){dialog, which ->
                val thirdLevel = edittext.text.toString()
                database.child("channels").child(arguments.classId).child(arguments.levelOneBranch).child(arguments.levelTwoBranch).child(thirdLevel).setValue("nothing")

            }
            setNegativeButton("Cancel"){dialog,which->}
            setView(dialogLayout)
            show()
        }
    }

    //prepare a list of folder names to be view in this screen. This list is to be passed to level adapter
    fun prepareLevelString(){
        database.child("channels").child(arguments.classId).child(arguments.levelOneBranch).child(arguments.levelTwoBranch).addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                viewModel.thirdLevelStrings.clear()
                val children = snapshot!!.children
                children.forEach {
                    viewModel.thirdLevelStrings.add(it.key.toString())
                    viewModel.thirdLevelStringsVM.value = viewModel.thirdLevelStrings
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
    }

    override fun onItemClick(position: String) {
//        TODO("Not yet implemented")
    }
}
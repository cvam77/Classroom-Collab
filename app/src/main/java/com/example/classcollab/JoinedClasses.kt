package com.example.classcollab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.CreatedClassesAdapter
import com.example.classcollab.databinding.FragmentJoinedClassesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//In this fragment, we show list all the classes joined by a user using class channel id

class JoinedClasses : Fragment(), CreatedClassesAdapter.OnItemClickListener {
    private lateinit var binding: FragmentJoinedClassesBinding
    private lateinit var createdClassesAdapter: CreatedClassesAdapter
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joined_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)

        binding = FragmentJoinedClassesBinding.bind(view)
        val recyclerView: RecyclerView = binding.rvCreatedClasses

        var emptyList = mutableListOf<String>()
        createdClassesAdapter = CreatedClassesAdapter(emptyList, context,this)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = createdClassesAdapter

        viewModel.joinedObserveClassIds.observe(viewLifecycleOwner, Observer {
            createdClassesAdapter.setDataset(it)
        })

        prepareArrayString()
    }

    //Prepare a list of names of joined classes by user
    fun prepareArrayString(){
        viewModel.joinedClassIds.clear()
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!.toString()
        database = Firebase.database.reference

        database.child("users").child(uid).child("joined").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val children = snapshot!!.children
                    children.forEach {
                        database.child("channels").child(it.key.toString()).child("channel_name").addValueEventListener(object:
                            ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val className = snapshot.value?.toString()

                                    if(className !=null)
                                    {
                                        viewModel.joinedClassIds.add(it.key.toString())
                                        viewModel.joinedObserveClassIds.value = viewModel.joinedClassIds

                                    }
    //                                val children = snapshot!!.children
    //                                children.forEach { println(it.value) }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                        })


                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })

    }

    override fun onItemClick(position: Int) {
        val classId = viewModel.joinedClassIds.get(position)

        val action = JoinedClassesDirections.actionJoinedClassesToIndividualClass(classId)
        view?.let { Navigation.findNavController(it).navigate(action) }
    }
}
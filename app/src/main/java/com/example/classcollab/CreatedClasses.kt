package com.example.classcollab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classcollab.RecyclerView.CreatedClassesAdapter
import com.example.classcollab.databinding.FragmentCreatedClassesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreatedClasses : Fragment() {

    private lateinit var binding: FragmentCreatedClassesBinding
    private lateinit var createdClassesAdapter: CreatedClassesAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_created_classes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreatedClassesBinding.bind(view)
        val recyclerView: RecyclerView = binding.rvCreatedClasses

        var arrayString = arrayOf<String>("Shivam")
        createdClassesAdapter = CreatedClassesAdapter(arrayString)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = createdClassesAdapter
    }

    fun prepareArrayString(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!.toString()
        database = Firebase.database.reference

        database.child("users").child(uid).child("created").addValueEventListener(ValueEventListener)
    }

}
package com.example.classcollab

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.classcollab.databinding.FragmentJoinAClassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinAClass : Fragment() {

    private lateinit var binding: FragmentJoinAClassBinding
    private lateinit var database: DatabaseReference
    lateinit var viewModel: ArrayStringViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_join_a_class, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentJoinAClassBinding.bind(view)
        viewModel = ViewModelProvider(this).get(ArrayStringViewModel::class.java)

        binding.joinClassBtn.setOnClickListener(View.OnClickListener {
            when{

                TextUtils.isEmpty(binding.classIdEt.text.toString().trim { it <= ' '}) ->{

                    Toast.makeText(context,
                            "Enter a valid class id",
                            Toast.LENGTH_SHORT).show()
                }

                else -> {
//                    activity?.supportFragmentManager?.popBackStack()
                    val channelId = binding.classIdEt.text.toString()

                    CheckChannelExist(channelId)
//                    AddChannelToDb(uid, randNum,channelName)
//                    Navigation.findNavController(view).navigate(R.id.action_createClassChannelFragment_to_mainScreenFragment)
                }
            }
        })

    }

    fun CheckChannelExist(channelId: String): Boolean
    {

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!.toString()
        database = Firebase.database.reference
        var classIDsList = mutableListOf<String>()
        database.child("channels").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot!!.children
                children.forEach {
                    classIDsList.add(it.key.toString())

                }
                if(classIDsList.contains(channelId))
                {
                    database.child("users").child(uid).child("joined").child(channelId).setValue("channel_id")
                }
                else{
                    Toast.makeText(context,
                            "Key does not exist",
                            Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return true
    }

}
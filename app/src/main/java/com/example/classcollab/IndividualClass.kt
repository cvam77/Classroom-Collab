package com.example.classcollab

import android.app.ActionBar
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.classcollab.databinding.FragmentIndividualClassBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class IndividualClass : Fragment() {

    private lateinit var binding: FragmentIndividualClassBinding
    val arguments: IndividualClassArgs by navArgs()
    private lateinit var database: DatabaseReference

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

        val classId = "Class Id: " + arguments.classId
        binding.classIdTv.text = classId

        database.child("channels").child(arguments.classId).child("channel_name").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val className = snapshot.value?.toString()
                println(className)
                binding.classNameTv.text = className
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.addNameFab.setOnClickListener(View.OnClickListener {
            OpenDialogBox()
        })

        binding.bookTv.setOnClickListener(View.OnClickListener { Navigation.findNavController(view).navigate(R.id.action_individualClass_to_book) })
    }

    fun OpenDialogBox(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.enter_name_et,null)
        val edittext = dialogLayout.findViewById<EditText>(R.id.enter_name_edittext)

        with(builder){
            setTitle("Enter the new section name")
            setPositiveButton("OK"){dialog, which ->
                CreateNewTv(edittext.text.toString())
            }
            setNegativeButton("Cancel"){dialog,which->}
            setView(dialogLayout)
            show()
        }
    }

    private fun CreateNewTv(text: String) {
        database.child("channels").child(arguments.classId).child("channel_section").push().setValue(text)

        val tv = TextView(context)
        tv.textSize = 25f
        tv.text = text
        tv.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        tv.gravity=Gravity.CENTER
        tv.setTextColor(Color.parseColor("#000000"))
        tv.setBackgroundColor(Color.parseColor("#FF018786"))
        val space = 20
        tv.setPadding(space, space, space, space)
        (tv.layoutParams as LinearLayout.LayoutParams).setMargins(space, space, space, space)
//        <TextView
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:gravity="center"
//        android:text="Miscellaneous"
//        android:textSize="25sp"
//        android:textColor="@color/black"
//        android:id="@+id/misc_tv"
//        android:background="@color/teal_700"
//        android:padding="10dp"
//        android:layout_margin="10dp"
//        />
        binding.sectionListLl.addView(tv)
    }
}
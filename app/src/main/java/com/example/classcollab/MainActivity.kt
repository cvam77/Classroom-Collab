package com.example.classcollab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

//It's the only activity in our class. Rest all are fragments hosted by this activity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val createChannelBtn = findViewById<Button>(R.id.create_channel)
//        createChannelBtn.setOnClickListener(View.OnClickListener { view ->  Toast.makeText(this,"Create Channel Button clicked",Toast.LENGTH_SHORT).show()})


    }
}
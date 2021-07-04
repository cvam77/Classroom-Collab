package com.example.classcollab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CommentsViewModel: ViewModel() {
    var questionIDsList = mutableListOf<String>()
    val questionIDsListVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }
}
package com.example.classcollab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArrayStringViewModel : ViewModel(){

    var joinedClassIds = mutableListOf<String>()
    val joinedObserveClassIds : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var classNamesVM = mutableListOf<String>()
    val ArrayStringVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }
}
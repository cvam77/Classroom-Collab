package com.example.classcollab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArrayStringViewModel : ViewModel(){

    var commentIDsList = mutableListOf<String>()
    val commentIDsListVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var questionIDsList = mutableListOf<String>()
    val questionIDsListVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var levelStrings = mutableListOf<String>()
    val levelStringsVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var assignmentTopic = mutableListOf<String>()
    val assignmentTopicVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var thirdLevelStrings = mutableListOf<String>()
    val thirdLevelStringsVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var joinedClassIds = mutableListOf<String>()
    val joinedObserveClassIds : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    var classNamesVM = mutableListOf<String>()
    val ArrayStringVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }
}
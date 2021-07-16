package com.example.classcollab

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

//Just a viewmodel for this app
class ArrayStringViewModel : ViewModel(){

    //List of question ids
    var questionIDsMap = HashMap<String,MutableList<String>>()
    val questionIDsMapVM : MutableLiveData<HashMap<String, MutableList<String>>> by lazy {
        MutableLiveData<HashMap<String, MutableList<String>>>()
    }

    //Name of Folders
    var levelStrings = mutableListOf<String>()
    val levelStringsVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    //Names of topics you show when you open an individual class - tests, assignments, book, Misc
    var assignmentTopic = mutableListOf<String>()
    val assignmentTopicVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    /*It just contains the strings in the third level of the firebase. Level starts "After" class id. So, for example,
    for a particular class with a unique id, "book" is level 1, "chapters" of the book is level 2, "question numbers" of the chapter
    is level 3. So, thirdlevelStrings just holds any string in the third level of the firebase.
    It is done this way, so that same code can be used for different sections of a class: tests, assignments, book, Misc. All of these sections have
    their own levels
     */
    var thirdLevelStrings = mutableListOf<String>()
    val thirdLevelStringsVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    //contains ids of joined classes
    var joinedClassIds = mutableListOf<String>()
    val joinedObserveClassIds : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    //contains names of created classes
    var classNamesVM = mutableListOf<String>()
    val ArrayStringVM : MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }
}
package com.example.payshare

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase

class FirebaseDBHelper {

    companion object {
        private var db = FirebaseDatabase
            .getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("groups")

        fun readGroups(groupEventListener: ChildEventListener){
            db.addChildEventListener(groupEventListener)
        }
        /*
        fun setToDoItem(key: String, toDoItem: ToDoItem){
            db.child(key).setValue(toDoItem)
        }

        fun removeToDoItem(key: String){
            db.child(key).removeValue()
        }
        */
    }
}
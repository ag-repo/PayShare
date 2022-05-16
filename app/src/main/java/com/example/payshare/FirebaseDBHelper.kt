package com.example.payshare

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseDBHelper {

    companion object {
        /*
        private var db = FirebaseDatabase
            .getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("groups")
         */
        var db = FirebaseDatabase.getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app/")
        var myRef = db.getReference("groups")

        fun readGroups(groupEventListener: ChildEventListener){
            //db.addChildEventListener(groupEventListener)
            myRef.addChildEventListener(groupEventListener)
        }

        fun setNewGroup(groupName: String, groupObj: Group){
            //db.child(groupName).setValue(groupObj)
            myRef.child(groupName).setValue(groupObj)
        }
        /*
        fun removeToDoItem(key: String){
            db.child(key).removeValue()
        }
        */
    }
}
package com.example.payshare

import com.google.firebase.database.*

class FirebaseDBHelper {

    companion object {

        val db = FirebaseDatabase.getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app")
                                    .reference
                                    .child("groups")

        fun setListeners(groupEventListener: ChildEventListener){
            //db.addChildEventListener(groupEventListener)
            db.addChildEventListener(groupEventListener)
        }

        fun setNewGroup(groupName: String, groupObj: Group){
            //db.child(groupName).setValue(groupObj)
            db.child(groupName).setValue(groupObj)
        }

        //NON VA BENE, RISCRIVE L'OGGETTO GRUPPO PERDENDO IL PRECEDENTE
        fun setNewPayment(groupName: String, transaction: Transaction){
            db.child(groupName).child("transactions").setValue(transaction)
        }

    }
}
package com.example.payshare

import android.util.Log
import com.google.firebase.database.*
import kotlin.math.absoluteValue

class FirebaseDBHelper {

    companion object {

        val db = FirebaseDatabase.getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app")
                                    .reference
                                    .child("groups")

        fun setListeners(groupEventListener: ChildEventListener){
            db.addChildEventListener(groupEventListener)
        }

        fun setNewGroup(groupName: String, groupObj: Group){
            db.child(groupName).setValue(groupObj)
        }

        fun deleteGroup(groupName: String){
            db.child(groupName).removeValue()
        }

        fun deleteTransaction(groupName: String, transName: String, transactionAmount: Double){
            var transactionToFind = transName+"-"+transactionAmount.hashCode().absoluteValue.toString()
            db.child(groupName).child("transactions").child(transactionToFind).removeValue()
        }

        fun setNewPayment(groupName: String, transaction: Transaction){
            var transName = transaction.getTitolo()+"-"+transaction.getTotale().hashCode().absoluteValue.toString()
            db.child(groupName).child("transactions").child(transName).setValue(transaction)
        }

        fun saveStatistics(groupName: String, list: ArrayList<SingleMemberStat>){
            db.child(groupName).child("stats").setValue(list)
        }

    }
}
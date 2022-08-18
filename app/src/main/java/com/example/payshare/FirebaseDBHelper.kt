package com.example.payshare

import com.google.firebase.database.*
import kotlin.math.absoluteValue

class FirebaseDBHelper {

    companion object {

        private val db = FirebaseDatabase.getInstance("https://payshare-a08b2-default-rtdb.europe-west1.firebasedatabase.app")
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

        fun modifyTransaction(groupName: String, oldName: String, oldAmount: Double, newName: String, transaction: Transaction){
            var transactionToFind = oldName+"-"+oldAmount.hashCode().absoluteValue.toString()
            var newTransactionName = newName+"-"+transaction.getTotal().hashCode().absoluteValue.toString()
            db.child(groupName).child("transactions").child(transactionToFind).removeValue()
            db.child(groupName).child("transactions").child(newTransactionName).setValue(transaction)
        }

        fun setNewPayment(groupName: String, transaction: Transaction){
            var transName = transaction.getTitle()+"-"+transaction.getTotal().hashCode().absoluteValue.toString()
            db.child(groupName).child("transactions").child(transName).setValue(transaction)
        }

        fun saveStatistics(groupName: String, list: MutableList<SingleMemberStat>){
            db.child(groupName).child("stats").setValue(list)
        }

        fun saveHowToPay(groupName: String, list: MutableList<SingleMemberDebt>){
            db.child(groupName).child("saldi").setValue(list)
        }
    }
}
package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_modify_transaction.*
import kotlinx.android.synthetic.main.activity_modify_transaction.lv_pagatoDa
import kotlinx.android.synthetic.main.activity_modify_transaction.lv_pagatoPer

class ModifyTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_transaction)
        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("groupObj") as Group
        val passedTransaction = intent.extras?.get("transaction") as Transaction

        val members = groupObj.getGroupMembers()
        val tTitle = passedTransaction.getTitle()
        val tTotal = passedTransaction.getTotal()
        val tPayedBy = passedTransaction.getPayedBy()
        val tPayedFor = passedTransaction.getPayedFor()

        transaction_name.text = passedTransaction.getTitle()
        trans_name.hint = tTitle
        trans_amount.hint = tTotal.toString()

        var payedByModified = false
        var payedForModified = false

        val lvAdapterChecked = ArrayAdapter(this, android.R.layout.simple_list_item_checked,members)
        lv_pagatoDa.adapter = lvAdapterChecked
        lv_pagatoPer.adapter = lvAdapterChecked

        for(i in members.indices){      //Pre-seleziono i membri che erano memorizzati nella transazione
            if(tPayedBy.contains(members[i])){ lv_pagatoDa.setItemChecked(i, true) }
            if(tPayedFor.contains(members[i])){ lv_pagatoPer.setItemChecked(i, true) }
        }

        lvAdapterChecked.notifyDataSetInvalidated()

        btn_modifyTransaction.setOnClickListener {
            var newTitle = trans_name.text.toString()
            var newTotal = trans_amount.text.toString()

            if(newTitle == ""){
                newTitle = trans_name.hint.toString()
            }

            if(newTotal == ""){
                newTotal = trans_amount.hint.toString()
            }

            if(newTitle != tTitle || newTotal.toDouble() != tTotal || payedByModified || payedForModified) {
                val newTransaction = Transaction(newTitle, tPayedBy, tPayedFor, newTotal.toDouble())
                FirebaseDBHelper.modifyTransaction(groupObj.getGroupName(), tTitle, tTotal, newTitle, newTransaction)
                val intent = Intent(this, GroupActivity::class.java)
                intent.putExtra("group_obj", groupObj)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Uno dei parametri non è valido", Toast.LENGTH_LONG).show()
            }
        }

        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            if(!tPayedBy.contains(members[position])){
                tPayedBy.add(members[position]) //se non contiene il nome, viene aggiunto
            } else {
                tPayedBy.remove(members[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
            payedByModified = true
        }

        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            if(!tPayedFor.contains(members[position])){
                tPayedFor.add(members[position]) //se non contiene il nome, viene aggiunto
            } else {
                tPayedFor.remove(members[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
            payedForModified = true
        }

        back_to_group_act.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj",groupObj)
            startActivity(intent)
        }
    }
}
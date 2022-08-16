package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_modify_transaction.*
import kotlinx.android.synthetic.main.activity_modify_transaction.lv_pagatoDa
import kotlinx.android.synthetic.main.activity_modify_transaction.lv_pagatoPer
import kotlinx.android.synthetic.main.activity_register_new_payment.*

class ModifyTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_transaction)
        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("groupObj") as Group
        val passed_transaction = intent.extras?.get("transaction") as Transaction

        val members = groupObj.getGroupMembers()
        transaction_name.text = passed_transaction.getTitolo()
        val t_title = passed_transaction.getTitolo()
        trans_name.hint = t_title
        val t_totale = passed_transaction.getTotale()
        trans_amount.hint = t_totale.toString()
        val t_pagatoDa = passed_transaction.getPagatoDa()
        val t_pagatoPer = passed_transaction.getPagatoPer()

        var pagatoDaModified = false
        var pagatoPerModified = false

        val lv_adapter_checked = ArrayAdapter(this, android.R.layout.simple_list_item_checked,members)
        lv_pagatoDa.adapter = lv_adapter_checked
        lv_pagatoPer.adapter = lv_adapter_checked

        for(i in members.indices){//Pre-seleziono i membri che erano memorizzati nella transazione
            if(t_pagatoDa.contains(members[i])){ lv_pagatoDa.setItemChecked(i, true) }
            if(t_pagatoPer.contains(members[i])){ lv_pagatoPer.setItemChecked(i, true) }
        }

        lv_adapter_checked.notifyDataSetInvalidated()

        btn_modifyTransaction.setOnClickListener {
            val nuovoTitolo = trans_name.text.toString()
            val nuovoTotale = trans_amount.text.toString().toDouble()

            if(nuovoTitolo != t_title || nuovoTotale != t_totale || pagatoDaModified || pagatoPerModified) {
                val newTransaction = Transaction(nuovoTitolo, t_pagatoDa, t_pagatoPer, nuovoTotale)
                FirebaseDBHelper.modifyTransaction(groupObj.getGroupName(), t_title, t_totale, nuovoTitolo, newTransaction)

                val intent = Intent(this, GroupActivity::class.java)
                intent.putExtra("group_obj", groupObj)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Uno dei parametri non è valido", Toast.LENGTH_LONG).show()
            }
        }

        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            if(!t_pagatoDa.contains(members[position])){
                t_pagatoDa.add(members[position]) //se non contiene il nome, viene aggiunto
            } else {
                t_pagatoDa.remove(members[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
            pagatoDaModified = true
        }

        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            if(!t_pagatoPer.contains(members[position])){
                t_pagatoPer.add(members[position]) //se non contiene il nome, viene aggiunto
            } else {
                t_pagatoPer.remove(members[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
            pagatoPerModified = true
        }

        back_to_group_act.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj",groupObj)
            startActivity(intent)
        }
    }
}
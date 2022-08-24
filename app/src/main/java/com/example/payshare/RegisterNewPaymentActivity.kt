package com.example.payshare

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import kotlinx.android.synthetic.main.activity_register_new_payment.view.*
import kotlinx.android.synthetic.main.activity_register_new_payment.paymentName as paymentName1


class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)
        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("group_obj") as Group
        paymentRegisterHeader.text = "Nuova spesa per " + groupObj.getGroupName() //Setto testo per registrare spesa

        var whoPay = ArrayList<String>()
        var forWho = ArrayList<String>()
        var membersList =  groupObj.getGroupMembers()

        //Adapter multiple choice per selezionare chi paga e per chi
        val lv_adapter_checked = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,membersList)

        lv_pagatoDa.adapter = lv_adapter_checked
        lv_pagatoPer.adapter = lv_adapter_checked
        lv_adapter_checked.notifyDataSetInvalidated()

        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            if(!whoPay.contains(membersList[position])){
                whoPay.add(membersList[position]) //se non contiene il nome, viene aggiunto
            } else {
                whoPay.remove(membersList[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
        }

        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            if(!forWho.contains(membersList[position])){
                forWho.add(membersList[position]) //se non contiene il nome, viene aggiunto
            } else {
                forWho.remove(membersList[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
        }

        back_top.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj",groupObj)
            startActivity(intent)
        }

        addNewTransactionBtn.setOnClickListener{
            val paymentTitle = paymentName1.text.toString()
            val paymentTotal = paymentQuantity.text.toString().toDouble()

            if(paymentTitle != "" && paymentTotal != 0.0 && whoPay.isNotEmpty() && forWho.isNotEmpty()) {
                val newTransaction = Transaction(paymentTitle, whoPay, forWho, paymentTotal)
                FirebaseDBHelper.setNewPayment(groupObj.getGroupName(),newTransaction)

                val intent = Intent(this, GroupActivity::class.java)
                intent.putExtra("group_obj", groupObj)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Uno dei parametri non è valido", Toast.LENGTH_LONG).show()
            }
        }
    }

}
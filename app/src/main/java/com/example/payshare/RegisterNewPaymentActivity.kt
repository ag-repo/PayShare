package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import kotlinx.android.synthetic.main.activity_register_new_payment.paymentName as paymentName1


class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)
        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("group_obj") as Group
        var chiPaga = ArrayList<String>()
        var perChiPaga = ArrayList<String>()
        var memberList =  groupObj.getGroupMembers()
        //Setto testo per registrare spesa
        paymentRegisterHeader.text = "Nuova spesa per " + groupObj.getGroupName()

        //Adapter multiple choice per selezionare chi paga e per chi
        val lv_adapter_checked = ArrayAdapter(this, android.R.layout.simple_list_item_checked,memberList)
        lv_pagatoDa.adapter = lv_adapter_checked
        lv_pagatoPer.adapter = lv_adapter_checked
        lv_adapter_checked.notifyDataSetInvalidated()

        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            if(!chiPaga.contains(memberList[position])){
                chiPaga.add(memberList[position]) //se non contiene il nome, viene aggiunto
            } else {
                chiPaga.remove(memberList[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
        }

        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            if(!perChiPaga.contains(memberList[position])){
                perChiPaga.add(memberList[position]) //se non contiene il nome, viene aggiunto
            } else {
                perChiPaga.remove(memberList[position]) //se viene cliccato nuovamente, viene deselezionato ed elimanato dalla lista
            }
        }

        back_top.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj",groupObj)
            startActivity(intent)
        }

        addNewTransactionBtn.setOnClickListener{

            val titoloSpesa = paymentName1.text.toString()
            val totaleSpesa = paymentQuantity.text.toString().toDouble()

            if(titoloSpesa != "" && totaleSpesa != 0.0 && !chiPaga.isEmpty() && !perChiPaga.isEmpty()) {
                val newTransaction = Transaction(titoloSpesa, chiPaga, perChiPaga, totaleSpesa)
                //groupObj.addNewTransaction(newTransaction)
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
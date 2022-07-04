package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import kotlinx.android.synthetic.main.activity_register_new_payment.paymentName as paymentName1


class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)

        val groupObj = intent.extras?.get("group_obj") as Group
        var chiPaga = ArrayList<String>()
        var perChiPaga = ArrayList<String>()

        paymentRegisterHeader.text = "Nuova spesa per " + groupObj.getGroupName()
        //Log.i("REGISTERNEWPAY", gName.toString())

        var memberList =  groupObj.getGroupMembers()
        val pagatoDaAdapter = GroupMemberListAdapter(this, memberList)
        val pagatoPerAdapter = GroupMemberListAdapter(this, memberList)
        //inizializzo le list view con adapter corrispondenti
        lv_pagatoDa.adapter = pagatoDaAdapter
        lv_pagatoPer.adapter = pagatoPerAdapter
        lv_pagatoDa.invalidateViews()
        lv_pagatoPer.invalidateViews()

        //listener per catturare chi paga la spesa dalla listview
        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            chiPaga.add(memberList[position])
        }
        //listener per pagato per
        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            perChiPaga.add(memberList[position])
        }

        addNewTransactionBtn.setOnClickListener{

            //check che titolo e quantità siano valide
            val titoloSpesa : String = R.id.paymentName.toString()
            val totaleSpesa : Double = R.id.paymentQuantity.toDouble()
            if(titoloSpesa != "" && totaleSpesa != 0.0 && !chiPaga.isEmpty() && !perChiPaga.isEmpty()) {
                val newTransaction = Transaction(titoloSpesa, chiPaga, perChiPaga, totaleSpesa)
                groupObj.addNewTransaction(newTransaction)
                Log.i("PAGAMENTO-REGISTRATO!!", groupObj.getGroupTransactions().toString())

                //Se tutto corretto, passo alla nuova activity
                val intent = Intent(this, GroupActivity::class.java)
                intent.putExtra("group_obj", groupObj)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Uno dei parametri non è valido", Toast.LENGTH_LONG).show()
            }
        }
    }

}
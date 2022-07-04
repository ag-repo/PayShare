package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import kotlinx.android.synthetic.main.activity_register_new_payment.paymentName as paymentName1


class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)

        val gName = intent.extras?.get("group") //Nome del gruppo
        val groupObj = intent.extras?.get("group_obj") as Group
        var chiPaga = ArrayList<String>()
        var perChiPaga = ArrayList<String>()
        //Log.i("REGISTERNEWPAY", gName.toString())

        var memberList =  groupObj.getGroupMembers()
        val pagatoDaAdapter = GroupMemberListAdapter(this, memberList)
        val pagatoPerAdapter = GroupMemberListAdapter(this, memberList)
        //inizializzo le list view con adapter corrispondenti
        lv_pagatoDa.adapter = pagatoDaAdapter
        lv_pagatoPer.adapter = pagatoPerAdapter
        lv_pagatoDa.invalidateViews()
        lv_pagatoPer.invalidateViews()

        backViewPayment.setOnClickListener{
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
        //listener per catturare chi paga la spesa dalla listview
        lv_pagatoDa.setOnItemClickListener { lv_adapter, listViewItems, position, id ->
            chiPaga.add(memberList[position])
        }
        //listener per pagato per
        lv_pagatoPer.setOnItemClickListener{ lv_adapter, listViewItems, position, id ->
            perChiPaga.add(memberList[position])
        }

        addNewTransactionBtn.setOnClickListener{

            val newTransaction = Transaction(R.id.paymentName.toString(), chiPaga, perChiPaga, R.id.paymentQuantity.toDouble())
            groupObj.addNewTransaction(newTransaction)
            Log.i("REGISTERNEWPAY", groupObj.toString())

            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_name", gName as String)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }
    }

}
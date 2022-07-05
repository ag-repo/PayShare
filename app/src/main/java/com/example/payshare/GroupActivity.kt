package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.fragment_group_payments.*

class GroupActivity : AppCompatActivity() {

    val paymentsFragment = GroupPaymentsFragment()
    val statsFragment = GroupStatsFragment()
    lateinit var lv_spese_adapter : SimpleAdapter
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var listview_payments : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val groupObj = intent.extras?.get("group_obj") as Group
        val transactionsList = groupObj.getGroupTransactions() //arraylist di transazioni

        groupName.text = groupObj.getGroupName() //rimpiazzo textview con il nome del gruppo
        groupPartecipants.text = groupObj.getGroupMembersToString() //rimpiazzo texview con i partecipanti del gruppo

        //popolo la listaSpese con le transazioni del gruppo ricevuto
        for(i in transactionsList.indices){
            var listPayments = HashMap<String,Any>()
            listPayments["titolo_spesa"] = transactionsList[i].getTitolo()
            listPayments["obj_transaction"] = transactionsList[i]
            listaSpese.add(listPayments)
        }

        //Adapter per fragment spese
        lv_spese_adapter = SimpleAdapter(
            this,
            listaSpese,
            R.layout.summary_list_group_item_layout,
            arrayOf("titolo_spesa", "obj_transaction"),
            intArrayOf(R.id.tv_groupName, R.id.tv_groupDescr)
        )

        listview_payments = lv_payments
        listview_payments.adapter = lv_spese_adapter

        lv_spese_adapter.notifyDataSetChanged()
        Log.i("DATI_SPESE_LISTA ------>", listaSpese.toString())

        addPaymentsBtn.setOnClickListener{
            val intent = Intent(this, RegisterNewPaymentActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }
    }
}
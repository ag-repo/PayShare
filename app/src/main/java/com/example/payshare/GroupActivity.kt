package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val groupObj = intent.extras?.get("group_obj") as Group
        val transactionsList = groupObj.getGroupTransactions() //arraylist di transazioni

        groupName.text = groupObj.getGroupName() //rimpiazzo textview con il nome del gruppo
        groupPartecipants.text = groupObj.getGroupMembersToString() //rimpiazzo texview con i partecipanti del gruppo

        //Adapter per fragment spese
        lv_spese_adapter = SimpleAdapter(
            this,
            listaSpese,
            R.layout.summary_list_group_item_layout,
            arrayOf("titolo_spesa", "totale_spesa"),
            intArrayOf(R.id.tv_groupName, R.id.tv_groupDescr)
        )

        //frame payments gruppo
        val transaction = supportFragmentManager.beginTransaction() //quando clicco sul testo
        transaction.replace(R.id.paymentsStatsFragment, paymentsFragment) //rimpiazzo fragment
        transaction.commit()

        groupSpeseBtn.setOnClickListener{
            val transaction = supportFragmentManager.beginTransaction() //quando clicco sul testo
            transaction.replace(R.id.paymentsStatsFragment, paymentsFragment) //rimpiazzo fragment
            transaction.commit()
        }

        groupStatsBtn.setOnClickListener{
            val transaction = supportFragmentManager.beginTransaction() //quando clicco sul testo
            transaction.replace(R.id.paymentsStatsFragment, statsFragment) //rimpiazzo fragment
            transaction.commit()
        }

        addPaymentsBtn.setOnClickListener{
            val intent = Intent(this, RegisterNewPaymentActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }
    }
}
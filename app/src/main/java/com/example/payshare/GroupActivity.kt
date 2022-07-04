package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {

    val paymentsFragment = GroupPaymentsFragment()
    val statsFragment = GroupStatsFragment()
    lateinit var lv_spese_adapter : SimpleAdapter
    private var listaSpese = arrayListOf<HashMap<String,Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val gName = intent.extras?.get("group_name") //nome del gruppo passato - tipo String
        val groupObj = intent.extras?.get("group_obj") as Group
        val transactionsList = groupObj.getGroupTransactions() //arraylist di transazioni

        //popolo l'Hashmap per l'adapter
        for(i in transactionsList){
            //Controllo se transazione già presente
            val transazione = HashMap<String,Transaction>()
            //transazione.put(transactionsList[i].getTitolo(), transactionsList[i])
            //aggiungo transazione alla listaSpese
            //listaSpese.add(transazione)
        }

        groupName.text = gName.toString() //rimpiazzo textview con il nome del gruppo

        //Adapter per fragment spese
        lv_spese_adapter = SimpleAdapter(
            this,
            listaSpese,
            R.layout.summary_list_group_item_layout,
            arrayOf("groupName", "groupDescr"),
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
            intent.putExtra("group_name", gName as String)
            intent.putExtra("group_obj", groupObj as Group)
            startActivity(intent)
        }
    }
}
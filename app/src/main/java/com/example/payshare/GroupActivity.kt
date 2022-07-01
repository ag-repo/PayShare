package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {

    val paymentsFragment = GroupPaymentsFragment()
    val statsFragment = GroupStatsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val group = intent.extras?.get("group") //nome del gruppo passato
        groupName.text = group.toString() //rimpiazzo textview con il nome del gruppo
        //val g = returnGroupFromGroupList(groupName.text)

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
            intent.putExtra("group", group as HashMap<String,String>)
            startActivity(intent)
        }
    }
}
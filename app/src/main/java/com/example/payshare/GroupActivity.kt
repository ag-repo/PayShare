package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_group.*

class GroupActivity : AppCompatActivity() {

    val paymentsFragment = GroupPaymentsFragment()
    val statsFragment = GroupStatsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val gName = intent.extras?.get("group_name") //nome del gruppo passato - tipo String
        val groupObj = intent.extras?.get("group_obj")
        groupName.text = gName.toString() //rimpiazzo textview con il nome del gruppo
        //Log.i("OGGETTO GRUPPO INTENT", "GRUPPO: ${groupObj.toString()}")


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
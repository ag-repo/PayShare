package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_group_header.*

class GroupActivity : AppCompatActivity() {

    val headerFragment = GroupHeaderFragment()
    val paymentsFragment = GroupPaymentsFragment()
    val statsFragment = GroupStatsFragment()
    val bottomActions = GroupBottomActionsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val transaction1 = supportFragmentManager.beginTransaction() //quando clicco sul testo
        transaction1.replace(R.id.headerFragmentContainer, headerFragment) //rimpiazzo fragment
        transaction1.commit()

        val transaction2 = supportFragmentManager.beginTransaction() //quando clicco sul testo
        transaction2.replace(R.id.paymentsStatsFragment, paymentsFragment) //rimpiazzo fragment
        transaction2.commit()

        val transaction3 = supportFragmentManager.beginTransaction() //quando clicco sul testo
        transaction3.replace(R.id.bottomActionsFragmentContainer, bottomActions) //rimpiazzo fragment
        transaction3.commit()


        group_btn_spese.setOnClickListener{
            val transaction = supportFragmentManager.beginTransaction() //quando clicco sul testo
            transaction.replace(R.id.paymentsStatsFragment, paymentsFragment) //rimpiazzo fragment
            transaction.commit()
        }

        group_btn_stats.setOnClickListener{
            val transaction = supportFragmentManager.beginTransaction() //quando clicco sul testo
            transaction.replace(R.id.paymentsStatsFragment, statsFragment) //rimpiazzo fragment
            transaction.commit()
        }
    }
}
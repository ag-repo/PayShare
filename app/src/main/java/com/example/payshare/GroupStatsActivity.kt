package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.SimpleAdapter
import kotlinx.android.synthetic.main.activity_group_stats.*
import java.io.Serializable
import java.time.temporal.TemporalAmount

class GroupStatsActivity : AppCompatActivity() {

    lateinit var lv_stats_adapter : SingleMemberStatsListAdapter
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var listview_stats : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)

        val groupObj = intent.extras?.get("group_obj") as Group
        val dataToDisplay = groupObj.getGroupMembers()

        group_stats_name.text = groupObj.getGroupName()

        lv_stats_adapter = SingleMemberStatsListAdapter(this,dataToDisplay)
        listview_stats = lv_singleUserStats
        listview_stats.adapter = lv_stats_adapter


        //lv_stats_adapter = TransactionsListAdapter(this,arra)

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    data class Stats(private var nomeUtente:String, private var amount: Double){

        constructor() : this("",0.0)

        fun set(item: Stats){
            this.nomeUtente = item.nomeUtente
            this.amount = item.amount
        }

        fun getNomeUtente():String{
            return this.nomeUtente
        }

        fun getAmount():Double{
            return this.amount
        }

        fun setNomeUtente(nuovoNome: String){
            this.nomeUtente = nuovoNome
        }

        fun setAmount(newAmount: Double){
            this.amount = newAmount
        }
    }

    fun populateDataToDisplay(){

    }
}
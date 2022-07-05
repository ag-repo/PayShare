package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter

class GroupStatsActivity : AppCompatActivity() {

    lateinit var lv_stats_adapter : TransactionsListAdapter
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var listview_stats : ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)

        val groupObj = intent.extras?.get("group_obj") as Group

        //lv_stats_adapter = TransactionsListAdapter(this,arra)

    }
}
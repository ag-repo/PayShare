package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.payshare.FirebaseDBHelper.Companion.saveStatistics
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group_stats.*
import kotlin.math.absoluteValue

class GroupStatsActivity : AppCompatActivity() {

    private lateinit var lv_stats_adapter : SingleMemberStatsListAdapter
    private lateinit var lv_debt_adapter : SingleMemberDebtListAdapter
    private lateinit var listview_stats : ListView
    private lateinit var listview_debt : ListView
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var passed_group_name : String
    private lateinit var groupObj : Group
    private var membersToDisplay = ArrayList<String>()          //valori presi dal gruppo passato per intent
    private var listTransactions = arrayListOf<Transaction>()   //valori presi da DB tramite listeners
    private var statsToDisplay = mutableListOf<SingleMemberStat>()  //calcolate in base alle transazioni ricevute
    private var saldiToDisplay = ArrayList<SingleMemberDebt>()  //COME SALDARE --> DA FARE

    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private lateinit var groupChildListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)
        supportActionBar?.hide() //Tolgo barra nome app

        groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        membersToDisplay = groupObj.getGroupMembers()
        group_stats_name.text = passed_group_name //rimpiazzo nome gruppo nella view


        //for(i in membersToDisplay.indices){
        //    statsToDisplay.add(SingleMemberStat(membersToDisplay[i], 0.0, 0.0))
        //}


        lv_stats_adapter = SingleMemberStatsListAdapter(this,statsToDisplay)
        lv_debt_adapter = SingleMemberDebtListAdapter(this,saldiToDisplay)
        listview_stats = lv_groupStatisticsListView
        listview_debt = lv_comePagare
        listview_stats.adapter = lv_stats_adapter
        listview_debt.adapter = lv_debt_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener())

        lv_stats_adapter.notifyDataSetChanged()

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        iv_refresh_stats.setOnClickListener{
            Log.i("STATS-TO-DISPLAY", "BEFORE--> $statsToDisplay")
            //statsToDisplay = computeStatistics(listTransactions)
            Log.i("STATS-TO-DISPLAY", "AFTER--> $statsToDisplay")
        }
    }

    override fun onStart() {
        super.onStart()
        val groupChildListener = getGroupsEventListener()
        groupReference!!.addChildEventListener(groupChildListener)
        this.groupChildListener = groupChildListener
    }

    override fun onStop(){
        super.onStop()
        if(groupChildListener != null){
            groupReference!!.removeEventListener(groupChildListener)
        }
    }

    private fun getGroupsEventListener(): ChildEventListener{

        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousChildName: String?) {

                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName() == passed_group_name) {

                    dataSnap.child("stats")
                    val stats = dataSnap.child("stats").getValue<ArrayList<SingleMemberStat>>()

                    if(stats != null){
                        for(i in stats.indices){
                            if(!statsToDisplay.contains(stats[i])){
                                statsToDisplay.add(stats[i])
                            }
                        }
                    }
                    lv_stats_adapter.notifyDataSetChanged()

                    /*
                    dataSnap.child("transactions")
                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) { //listTransactions da popolare con oggetti transaction
                        var i: Int = 0
                        for ((key, value) in transactions) {
                            listaSpese.add(i, value as HashMap<String,Any>)
                            val t = Transaction(
                                value["titolo"] as String,
                                value["pagatoDa"] as ArrayList<String>,
                                value["pagatoPer"] as ArrayList<String>,
                                (value["totale"] as Long).toDouble()
                            )
                            listTransactions.add(t)
                            i += 1
                            //statsToDisplay = computeStatistics(listTransactions)
                            //saveStatistics(passed_group_name, statsToDisplay)
                        }
                    }
                    lv_stats_adapter.notifyDataSetChanged()*/
                }
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                dataSnap.child("transactions")
                val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()

                if (item?.getGroupName() == passed_group_name) {
                    if (transactions != null){
                        for((key,value) in transactions){
                            //listaSpese add??
                            val spesa = value as HashMap<String,Any>
                            val trans = Transaction(
                                spesa["titolo"] as String,
                                spesa["pagatoDa"] as ArrayList<String>,
                                spesa["pagatoPer"] as ArrayList<String>,
                                (spesa["totale"] as Long).toDouble()
                            )
                            if(!listTransactions.contains(trans)){
                                listTransactions.add(trans)
                            }
                        }
                    }
                    //statsToDisplay = computeStatistics(listTransactions)
                    Log.i("CHILD-Change","statToDisplay--> $statsToDisplay")
                    lv_stats_adapter.notifyDataSetInvalidated()
                    lv_stats_adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                dataSnap.child("transactions")
                val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()

                if (item?.getGroupName() == passed_group_name) {
                    if (transactions != null){
                        for((key,value) in transactions){
                            //listaSpese add??
                            val spesa = value as HashMap<String,Any>
                            val trans = Transaction(
                                spesa["titolo"] as String,
                                spesa["pagatoDa"] as ArrayList<String>,
                                spesa["pagatoPer"] as ArrayList<String>,
                                (spesa["totale"] as Long).toDouble()
                            )
                            if(!listTransactions.contains(trans)){
                                listTransactions.remove(trans)
                            }
                        }
                    }
                    //statsToDisplay = computeStatistics(listTransactions)
                    //Log.i("CHILD-Rem","statToDisplay--> $statsToDisplay")
                    lv_stats_adapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(dataSnap: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        return listener
    }
}
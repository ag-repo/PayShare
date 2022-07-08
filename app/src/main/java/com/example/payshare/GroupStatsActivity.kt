package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group_stats.*

class GroupStatsActivity : AppCompatActivity() {

    lateinit var lv_stats_adapter : SingleMemberStatsListAdapter
    private lateinit var listview_stats : ListView
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var passed_group_name : String
    private var membersToDisplay = arrayListOf<String>()
    private var listTransactions = arrayListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        val membersToDisplay = groupObj.getGroupMembers()
        group_stats_name.text = groupObj.getGroupName() //rimpiazzo nome gruppo nella view

        lv_stats_adapter = SingleMemberStatsListAdapter(this,membersToDisplay)
        listview_stats = lv_groupStatisticsListView
        listview_stats.adapter = lv_stats_adapter
        FirebaseDBHelper.setListeners(getGroupsEventListener())
        lv_stats_adapter.notifyDataSetChanged()

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        iv_refresh_stats.setOnClickListener{
            val map = computeStatistics(groupObj,listTransactions)
            Log.i("COMPUTESTATISTICS", map.toString())
            Log.i("LISTTRANSACTION", listTransactions.toString()) //QUI LISTTRANS è VUOTA
        }
    }

    private fun getGroupsEventListener(): ChildEventListener{
        val adapter = lv_stats_adapter

        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName().equals(passed_group_name)) {
                    for(i in item.getGroupMembers().indices)
                        membersToDisplay.add(item.getGroupMembers()[i])
                }
                dataSnap.child("transactions")
                val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()

                if(item?.getGroupName() == passed_group_name) {
                    //listTransactions da popolare con oggetti transaction
                    if (transactions != null) {
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
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                TODO("Not yet implemented")
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

    fun computeStatistics(groupObj: Group, listTransactions: ArrayList<Transaction>): HashMap<String,Double>{

        val membri = groupObj.getGroupMembers()
        var data: HashMap<String, Double> = HashMap()

        Log.i("GR-STATS-datain", listTransactions.toString())

        //popolo data con NomePartecipante, Array delle spese dandogli i nomi dei partecipanti
        for(i in membri.indices){
            data.put(membri[i], 0.0)
        }

        for(i in listTransactions.indices){
            //calcolo il totale splittato per chi paga
            val splittedAmount = listTransactions[i].getTotale() / listTransactions[i].getPagatoDa().size
            val splittedDebt = listTransactions[i].getTotale() / listTransactions[i].getPagatoPer().size

            Log.i("GR-STATS-transaction", listTransactions[i].toString())
            Log.i("GR-STATS-splittedAmount", splittedAmount.toString())
            Log.i("GR-STATS-splittedDebt", splittedDebt.toString())

            for(i in listTransactions.indices){
                val pagatoDa = listTransactions[i].getPagatoDa()
                for(i in pagatoDa.indices){
                    data[pagatoDa[i]] = data[pagatoDa[i]]!!.toDouble() + splittedAmount
                }
            }
            Log.i("FOR-DATA1", data.toString())

            for(i in listTransactions.indices){
                val pagatoPer = listTransactions[i].getPagatoPer()
                for(i in pagatoPer.indices){
                    data[pagatoPer[i]] = data[pagatoPer[i]]!!.toDouble() - splittedDebt
                }
            }
            Log.i("FOR-DATA2", data.toString())
        }
        return data
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
}
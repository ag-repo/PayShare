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

        //val map = computeStatistics(groupObj)
        //Log.i("COMPUTESTATISTICS", map.toString())

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getGroupsEventListener(): ChildEventListener{
        val adapter = lv_stats_adapter

        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {
                    for(i in item.getGroupMembers().indices)
                        membersToDisplay.add(item.getGroupMembers()[i])
                        Log.i("COMPUTESTATISTICS", membersToDisplay.toString())
                }
                dataSnap.child("transactions")
                val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()

                if(item?.getGroupName() == passed_group_name) {
                    //listTransactions da popolare con oggetti transaction
                    if (transactions != null) {
                        var i: Int = 0
                        for ((key, value) in transactions) {
                            listaSpese.add(i, value as HashMap<String,Any>)
                            val trans = Transaction(
                                value["titolo"] as String,
                                value["pagatoDa"] as ArrayList<String>,
                                value["pagatoPer"] as ArrayList<String>,
                                (value["totale"] as Long).toDouble()
                            )
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

    fun computeStatistics(groupObj: Group): HashMap<String,Double>{

        val membri = groupObj.getGroupMembers()
        lateinit var data: HashMap<String,Double>
        val transactions = groupObj.getGroupTransactions()

        //val myArray = ArrayList<Double>()

        //popolo data con NomePartecipante, Array delle spese dandogli i nomi dei partecipanti
        for(i in membri.indices){
            data.put(membri[i], 0.0)
        }

        for(i in transactions.indices){
            //calcolo il totale splittato per chi paga
            val splittedAmount = transactions[i].getTotale() / transactions[i].getPagatoDa().size
            val splittedDebt = transactions[i].getTotale() / transactions[i].getPagatoPer().size
            //aggiungo il totale splittato al vettore per il calcolo in corrispondenza del nome
            for(i in membri.indices){
                //se nella transazione ho tra chi ha pagato uno dei membri su cui ciclo, allora aggiunto importo positivo
                if(transactions[i].getPagatoDa().contains(membri[i]))
                data.put(membri[i], +splittedAmount)
            }
            //faccio lo stesso però aggiungendo un numero negativo
            for(i in membri.indices){
                //se nella transazione ho tra chi ha pagato uno dei membri su cui ciclo, allora aggiunto importo positivo
                if(transactions[i].getPagatoPer().contains(membri[i]))
                    data.put(membri[i], -splittedDebt)
            }
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
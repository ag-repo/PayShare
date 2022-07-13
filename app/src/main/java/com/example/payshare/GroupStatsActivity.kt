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
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

class GroupStatsActivity : AppCompatActivity() {

    lateinit var lv_stats_adapter : SingleMemberStatsListAdapter
    private lateinit var listview_stats : ListView
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var passed_group_name : String
    private var statistics = HashMap<String,Double>()
    private var membersToDisplay = ArrayList<String>()
    private var listTransactions = arrayListOf<Transaction>()
    private var statsToDisplay = ArrayList<SingleMemberStat>()
    private var saldiToDisplay = ArrayList<SingleMemberDebt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        membersToDisplay = groupObj.getGroupMembers()
        group_stats_name.text = groupObj.getGroupName() //rimpiazzo nome gruppo nella view

        lv_stats_adapter = SingleMemberStatsListAdapter(this,statsToDisplay)
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
            thread(start=true){
                statistics = computeStatistics(groupObj,listTransactions)
                Log.i("STATISTICS", statistics.toString())

                //saldiToDisplay = computeDebt(groupObj, statistics)
                //Log.i("DEBITI-CALCOLATI", saldiToDisplay.toString())

            }

            //converto Hashmap in oggetto SingleMemberStat per la visualizzazione
            for ((key, value) in statistics) {
                statsToDisplay.add(SingleMemberStat(key,value))
            }
            lv_stats_adapter.notifyDataSetChanged()
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
                }
                adapter.notifyDataSetChanged()
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
                }
                adapter.notifyDataSetChanged()
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

    private fun computeStatistics(groupObj: Group, listTransactions: ArrayList<Transaction>): HashMap<String,Double>{

        val membri = groupObj.getGroupMembers()
        var data: HashMap<String, Double> = HashMap()

        for(i in membri.indices){ data[membri[i]] = 0.0 } //popolo data con NomePartecipante, Array delle spese dandogli i nomi dei partecipanti

        for(transaction in listTransactions.indices){
            val splittedAmount = listTransactions[transaction].getTotale() / listTransactions[transaction].getPagatoDa().size
            val splittedDebt = listTransactions[transaction].getTotale() / listTransactions[transaction].getPagatoPer().size
            val pagatoDa = listTransactions[transaction].getPagatoDa()
            val pagatoPer = listTransactions[transaction].getPagatoPer()

            for(i in pagatoDa.indices){
                val temp = data[pagatoDa[i]]
                data[pagatoDa[i]] = temp!!+splittedAmount
            }

            for(i in pagatoPer.indices){
                val temp = data[pagatoPer[i]]
                data[pagatoPer[i]] = temp!!-splittedDebt
            }
        }
        return data
    }

    //listDebt sarà l'hashmap computata da "computeStatistics()" che contiente "nome","amount"
    private fun computeDebt(groupObj: Group, listDebt: HashMap<String,Double>): ArrayList<SingleMemberDebt>{
        val membri = groupObj.getGroupMembers()
        var debiti = ArrayList<SingleMemberDebt>()

        var membriInPositivo = ArrayList<SingleMemberStat>()
        var membriInNegativo = ArrayList<SingleMemberStat>()

        //scansiono i dati computati per vedere chi è in negativo e chi in positivo
        for((key,value) in listDebt){
            if(listDebt.getValue(key)>0){
                membriInPositivo.add(SingleMemberStat(key, value))
            }
            if(listDebt.getValue(key)<0){
                membriInNegativo.add(SingleMemberStat(key, value))
            }
            membriInNegativo.sortByDescending{ it.getMemberAmount() } //ordino dal più grande al più piccolo
            membriInPositivo.sortByDescending{ it.getMemberAmount() } //ordino dal più grande al più piccolo
        }

        for(i in membriInPositivo.indices){
            //se il positivo - il negativo è ancora > 0, assegno il pagamento del debito
            var debitoDaSaldare = membriInPositivo[i].getMemberAmount()
            while(debitoDaSaldare>0){
                if(membriInPositivo[i].getMemberAmount() - membriInNegativo[i].getMemberAmount() > 0){
                    debiti.add(
                        SingleMemberDebt(
                            membriInPositivo[i].getMemberName(),
                            membriInNegativo[i].getMemberName(),
                            membriInNegativo[i].getMemberAmount().absoluteValue)
                    )
                    debitoDaSaldare-membriInNegativo[i].getMemberAmount()
                }
            }
        }
        Log.i("DEBITIIIII", debiti.toString())
        return debiti
    }

    data class SingleMemberStat(private var memberName: String, private var memberAmount: Double){

        constructor() : this("", 0.0)

        fun set(stat: SingleMemberStat){
            memberName = stat.memberName
            memberAmount = stat.memberAmount
        }

        fun getMemberName(): String{
            return this.memberName
        }

        fun getMemberAmount(): Double{
            return this.memberAmount
        }
    }

    data class SingleMemberDebt(private var ricevente: String, private var pagante: String, private var debito: Double){
        constructor() : this("", "", 0.0)

        fun set(debt: SingleMemberDebt){
            ricevente = debt.ricevente
            pagante = debt.pagante
            debito = debt.debito
        }

        fun getPagante(): String{
            return this.pagante
        }

        fun getRicevente(): String{
            return this.ricevente
        }

        fun getDebito(): Double{
            return this.debito
        }
    }
}
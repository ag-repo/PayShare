package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ListView
import androidx.core.view.iterator
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group_stats.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

class GroupStatsActivity : AppCompatActivity() {

    lateinit var lv_stats_adapter : SingleMemberStatsListAdapter
    lateinit var lv_debt_adapter : SingleMemberDebtListAdapter
    lateinit var lv_single_total_adapter : SingleMemberStatsListAdapter
    private lateinit var listview_stats : ListView
    private lateinit var listview_debt : ListView
    private lateinit var listview_single_total : ListView
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var passed_group_name : String
    private lateinit var groupObj : Group
    private var statistics = HashMap<String,Double>()           //HashMap di String = nome, Double = totale debito/credito
    private var single_statistics = HashMap<String, Double>()
    private var membersToDisplay = ArrayList<String>()          //valori presi dal gruppo passato per intent
    private var listTransactions = arrayListOf<Transaction>()   //valori presi da DB tramite listeners
    private var statsToDisplay = ArrayList<SingleMemberStat>()  //calcolate in base alle transazioni ricevute
    private var saldiToDisplay = ArrayList<SingleMemberDebt>()  //COME SALDARE --> DA FARE
    private var singleTotalToDisplay = ArrayList<SingleMemberStat>()

    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private lateinit var groupChildListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)
        supportActionBar?.hide() //Tolgo barra nome app

        groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        membersToDisplay = groupObj.getGroupMembers()
        group_stats_name.text = groupObj.getGroupName() //rimpiazzo nome gruppo nella view

        lv_stats_adapter = SingleMemberStatsListAdapter(this,statsToDisplay)
        lv_debt_adapter = SingleMemberDebtListAdapter(this,saldiToDisplay)
        lv_single_total_adapter = SingleMemberStatsListAdapter(this, singleTotalToDisplay)

        listview_stats = lv_groupStatisticsListView
        listview_debt = lv_comePagare
        listview_single_total = lv_personal_stats

        listview_stats.adapter = lv_stats_adapter
        listview_debt.adapter = lv_debt_adapter
        listview_single_total.adapter = lv_single_total_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener())
        lv_stats_adapter.notifyDataSetChanged()
        lv_debt_adapter.notifyDataSetChanged()
        lv_single_total_adapter.notifyDataSetChanged()


        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        iv_refresh_stats.setOnClickListener{
            statistics = computeStatistics(groupObj,listTransactions)
            saldiToDisplay = computeComeSaldare(statistics)
            single_statistics = computeSingleTotal(groupObj, listTransactions)
            Log.i("COMPUTE-STATISTICS", statistics.toString())
            Log.i("COMPUTE-DEBT", saldiToDisplay.toString())
            Log.i("COMPUTE-SINGLE", single_statistics.toString())

            //converto Hashmap in oggetto SingleMemberStat per la visualizzazione
            for ((key, value) in statistics) {
                val singleMemberStat = SingleMemberStat(key,value)
                if(!statsToDisplay.contains(singleMemberStat)){
                    statsToDisplay.add(singleMemberStat)
                }
            }

            for(i in saldiToDisplay.indices){
                val singleMemberDebt = SingleMemberDebt(saldiToDisplay[i].getRicevente(), saldiToDisplay[i].getPagante(), saldiToDisplay[i].getDebito())
                //print(singleMemberDebt.toString())
                //print(saldiToDisplay[i].toString())

                //ATTENZIONE INSERIRE NUOVAMENTE CONTROLLO!!!!!!!!!!
                saldiToDisplay.add(singleMemberDebt)
                //QUI NON ENTRA MAI !?!?!?!?!?
                if(!saldiToDisplay.contains(singleMemberDebt)){
                    saldiToDisplay.add(singleMemberDebt)
                }
            }

            lv_stats_adapter.notifyDataSetChanged()
            lv_debt_adapter.notifyDataSetChanged()
            lv_single_total_adapter.notifyDataSetChanged()
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

        val adapter = lv_stats_adapter
        //TEST
        val debt_adapter = lv_debt_adapter

        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName() == passed_group_name) {
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
                debt_adapter.notifyDataSetChanged()
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
                debt_adapter.notifyDataSetChanged()
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
                debt_adapter.notifyDataSetChanged()
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

    //calcola quanto ogni partecipante è in positivo o negativo
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

    //calcola quanto ha speso ogni singolo membro del gruppo
    private fun computeSingleTotal(groupObj: Group, listTransactions: ArrayList<Transaction>): HashMap<String,Double>{

        val membri = groupObj.getGroupMembers()             //prendo la lista dei membri dal gruppo passato
        var data: HashMap<String, Double> = HashMap()       //variabile da ritornare

        for(i in membri.indices){ data[membri[i]] = 0.0 }   //popolo data con NomePartecipante, Array delle spese dandogli i nomi dei partecipanti

        for(transaction in listTransactions.indices){
            var transactionSplit = listTransactions[transaction].getTotale() / listTransactions[transaction].getPagatoDa().size
            var transactionSubjects = listTransactions[transaction].getPagatoDa() //prendo solo chi ha pagato delle transazioni

            for(i in transactionSubjects.indices){
                for((key,value) in data){
                    if(key == transactionSubjects[i]){              //se trovo il nome uguale
                        data[key] = value + transactionSplit        //aggiungo amount spesa a data
                    }
                }
            }
        }

        return data
    }

    //calcola come saldare i debiti attuali
    private fun computeComeSaldare(listDebt: HashMap<String, Double>): ArrayList<SingleMemberDebt>{
        var debiti = ArrayList<SingleMemberDebt>()          //variabile da ritornare
        var membriPos = ArrayList<SingleMemberStat>()       //lista dei membri in positivo
        var membriNeg = ArrayList<SingleMemberStat>()       //lista dei membri in negativo
        var iPos = 0                                        //counter per il while membri in positivo
        var iNeg = 0                                        //counter per il while membri in positivo

        //listDebt = HashMap di String = nome, Double = totale debito/credito
        for((key,value) in listDebt){
            val membStat = SingleMemberStat(key, value)
            if(membStat.getMemberAmount() > 0){             //se l'amount è positivo
                membriPos.add(membStat)                     //aggiungo alla lista dei positivi
            } else {
                membriNeg.add(membStat)                     //altrimenti aggiungo alla lista dei negativi
            }
        }

        membriPos.sortByDescending{ it.getMemberAmount() }  //ordino i membri positivi, da chi ha speso di più a chi ha speso di meno
        membriNeg.sortBy{ it.getMemberAmount() }            //li ordino dal più piccolo al più grande, avendo numeri negativi

        while(iPos < membriPos.size && iNeg < membriNeg.size){      //fichè i contatori puntano a regioni di memoria valide
            val p = membriPos[iPos]                                 //p è un SingleMemberStat con amount positivo
            val n = membriNeg[iNeg]                                 //n è un SingleMemberStat con amount negativo

            if(p.getMemberAmount() >= n.getMemberAmount().absoluteValue){           //se amount positivo >= amount negativo
                debiti.add(SingleMemberDebt(p.getMemberName(), n.getMemberName(), n.getMemberAmount().absoluteValue))   //aggiungo a debiti un debito da pagare
                iNeg ++                                                             //aumento i negativi per andare sul prossimo dato che ho saldato il primo della lista
                p.setAmount(p.getMemberAmount() + n.getMemberAmount())              //setto il nuovo debito da saldare per il positivo togliendo l'amount del negativo
                //if((p.getMemberAmount() + n.getMemberAmount()) <= 0.0){ iPos++ }
            } else {
                debiti.add(SingleMemberDebt(p.getMemberName(), n.getMemberName(), p.getMemberAmount().absoluteValue))
                iPos ++
                membriNeg[iNeg].setAmount((n.getMemberAmount() + p.getMemberAmount()))
                //if(((n.getMemberAmount() + p.getMemberAmount()).absoluteValue).equals(0.0)){ iNeg++ }
            }
        }
        return debiti
    }

    //dataclass per le statistiche singole dei membri del gruppo
    data class SingleMemberStat(private var memberName: String, private var memberAmount: Double){

        constructor() : this("", 0.0)

        fun set(stat: SingleMemberStat){
            memberName = stat.memberName
            memberAmount = stat.memberAmount
        }

        fun setAmount(amount: Double){
            this.memberAmount = amount
        }

        fun setName(name: String){
            this.memberName = name
        }

        fun getMemberName(): String{
            return this.memberName
        }

        fun getMemberAmount(): Double{
            return this.memberAmount
        }
    }
    //dataclass per capire come saldare i debiti
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
package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
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
    private var statsToDisplay = ArrayList<SingleMemberStat>()  //calcolate in base alle transazioni ricevute
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

        lv_stats_adapter = SingleMemberStatsListAdapter(this,statsToDisplay)
        lv_debt_adapter = SingleMemberDebtListAdapter(this,saldiToDisplay)
        listview_stats = lv_groupStatisticsListView
        listview_debt = lv_comePagare
        listview_stats.adapter = lv_stats_adapter
        listview_debt.adapter = lv_debt_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener())

        for(i in membersToDisplay.indices){
            statsToDisplay.add(SingleMemberStat(membersToDisplay[i], 0.0, 0.0))
        }
        lv_stats_adapter.notifyDataSetChanged()

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        iv_refresh_stats.setOnClickListener{
            Log.i("STATS-TO-DISPLAY", "BEFORE--> $statsToDisplay")
            statsToDisplay = computeStatistics(membersToDisplay,listTransactions)
            Log.i("STATS-TO-DISPLAY", "AFTER--> $statsToDisplay")
            lv_stats_adapter.notifyDataSetInvalidated()
            lv_stats_adapter.notifyDataSetChanged()

            /*
            Thread(Runnable {
                statsToDisplay = computeStatistics(groupObj,listTransactions)
                //converto Hashmap in oggetto SingleMemberStat per la visualizzazione
                Log.i("STATS-TO-DISPLAY", statsToDisplay.toString())

                saldiToDisplay = computeComeSaldare(statsToDisplay)
                Log.i("COMPUTE-DEBT", saldiToDisplay.toString())


                Handler(mainLooper).post{
                    lv_stats_adapter.notifyDataSetChanged()
                    Log.i("NOTIFY-ADAPT1","Done")
                    lv_debt_adapter.notifyDataSetChanged()
                    Log.i("NOTIFY-ADAPT2","Done")
                }
            }).start() */
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
                        }
                    }
                    statsToDisplay = computeStatistics(membersToDisplay,listTransactions)
                    Log.i("CHILD-Added","statToDisplay--> $statsToDisplay")
                    lv_stats_adapter.notifyDataSetInvalidated()
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
                    statsToDisplay = computeStatistics(membersToDisplay,listTransactions)
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
                    statsToDisplay = computeStatistics(membersToDisplay,listTransactions)
                    Log.i("CHILD-Rem","statToDisplay--> $statsToDisplay")
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

    //calcola quanto ogni partecipante è in positivo o negativo e la sua spesa totale individuale
    private fun computeStatistics(members: ArrayList<String>, listTransactions: ArrayList<Transaction>): ArrayList<SingleMemberStat>{
        var data =  ArrayList<SingleMemberStat>()
        var tempStat = HashMap<String,Double>()
        var tempSingleAmount = HashMap<String,Double>()

        for(i in members.indices){      //inizializzo hashmap con nomi partecipanti
            tempStat[members[i]] = 0.0
            tempSingleAmount[members[i]] = 0.0
        }

        for(transaction in listTransactions.indices){
            val splittedAmount = listTransactions[transaction].getTotale() / listTransactions[transaction].getPagatoDa().size
            val splittedDebt = listTransactions[transaction].getTotale() / listTransactions[transaction].getPagatoPer().size
            val pagatoDa = listTransactions[transaction].getPagatoDa()
            val pagatoPer = listTransactions[transaction].getPagatoPer()

            for(i in pagatoDa.indices){
                val t1 = tempStat[pagatoDa[i]]
                val t2 = tempSingleAmount[pagatoDa[i]]
                tempStat[pagatoDa[i]] = t1!!+splittedAmount
                tempSingleAmount[pagatoDa[i]] = t2!!+splittedAmount
            }

            for(i in pagatoPer.indices){
                val t = tempStat[pagatoPer[i]]
                tempStat[pagatoPer[i]] = t!!-splittedDebt
            }
        }

        for(i in members.indices){
            data.add(SingleMemberStat(members[i], tempStat[members[i]]!!, tempSingleAmount[members[i]]!!))
        }
        Log.i("COMPUTE-STATS","dataToReturn--> $data")
        return data
    }

    //calcola come saldare i debiti attuali
    private fun computeComeSaldare(listDebt: ArrayList<SingleMemberStat>): ArrayList<SingleMemberDebt>{
        var debiti = ArrayList<SingleMemberDebt>()          //variabile da ritornare
        var membriPos = ArrayList<SingleMemberStat>()       //lista dei membri in positivo
        var membriNeg = ArrayList<SingleMemberStat>()       //lista dei membri in negativo
        var iPos = 0                                        //counter per il while membri in positivo
        var iNeg = 0                                        //counter per il while membri in positivo

        //listDebt = HashMap di String = nome, Double = totale debito/credito
        for(i in listDebt.indices){
            val membStat = SingleMemberStat(listDebt[i].getMemberName(), listDebt[i].getMemberAmount(), listDebt[i].getSingleMemberTotal())
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
                debiti.add(SingleMemberDebt(p.getMemberName(), n.getMemberName(), p.getMemberAmount().absoluteValue))   //aggiungo a debiti un debito da pagare
                iPos ++                                                             //aumento i positivi perchè sicuramente ho saldato il debito
                membriNeg[iNeg].setAmount((n.getMemberAmount() + p.getMemberAmount())) //setto il nuovo debito da saldare per il negativo
                //if(((n.getMemberAmount() + p.getMemberAmount()).absoluteValue).equals(0.0)){ iNeg++ }
            }
        }
        return debiti
    }
}

/*/
    private fun calculateViews(){

        statsToDisplay = computeStatistics(groupObj,listTransactions)
        //converto Hashmap in oggetto SingleMemberStat per la visualizzazione
        for (i in statsToDisplay.indices) {
            val singleMemberStat = SingleMemberStat(statsToDisplay[i].getMemberName(), statsToDisplay[i].getMemberAmount(),statsToDisplay[i].getSingleMemberTotal())
            if(!statsToDisplay.contains(singleMemberStat)){
                statsToDisplay.add(singleMemberStat)
            }
        }
        Log.i("COMPUTE-STATISTICS", statsToDisplay.toString())
        Log.i("STATS-TO-DISPLAY", statsToDisplay.toString())


        saldiToDisplay = computeComeSaldare(statsToDisplay)
        Log.i("COMPUTE-DEBT", saldiToDisplay.toString())

        single_statistics = computeSingleTotal(groupObj, listTransactions)
        Log.i("COMPUTE-SINGLE", single_statistics.toString())

    }*/
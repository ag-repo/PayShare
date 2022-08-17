package com.example.payshare

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class GroupActivity : AppCompatActivity() {

    private lateinit var lv_spese_adapter : TransactionsListAdapter
    private lateinit var listview_payments : ListView
    private lateinit var passed_group_name : String
    private lateinit var passed_group_members : ArrayList<String>
    private lateinit var groupChildListener: ChildEventListener
    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private var listTransactions = arrayListOf<Transaction>() //Lista delle transazioni da mostrare in ListView
    private var statsToSave = mutableListOf<SingleMemberStat>()
    private var saldiToSave = mutableListOf<SingleMemberDebt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName() //Modifiche delle scritte della view
        passed_group_members = groupObj.getGroupMembers()
        groupName.text = passed_group_name //rimpiazzo textview con il nome del gruppo

        lv_spese_adapter = TransactionsListAdapter(this, listTransactions)  //inizializzo la listview
        listview_payments = group_list_view
        listview_payments.adapter = lv_spese_adapter
        FirebaseDBHelper.setListeners(getGroupsEventListener());

        listview_payments.setOnItemLongClickListener { adapterView, view, position, l ->
            var trans = listTransactions[position]
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Confermi di volere eliminare la transazione?")
                .setCancelable(false)
                .setPositiveButton("SI", DialogInterface.OnClickListener { dialog, id ->
                    Log.i("LongClick","--> listTransaBEFORE = $listTransactions")
                    listTransactions.remove(trans)
                    Log.i("LongClick","--> listTransaAFTER = $listTransactions")
                    FirebaseDBHelper.deleteTransaction(passed_group_name, trans.getTitolo(), trans.getTotale())
                    lv_spese_adapter.notifyDataSetChanged()
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("Elimina transazione")
            alert.show()
            //lv_spese_adapter.notifyDataSetChanged()
            true
        }

        listview_payments.setOnItemClickListener{ lv_adapter,listViewItems, position, id ->
            var trans = listTransactions[position]
            val intent = Intent(this, ModifyTransactionActivity::class.java)
            intent.putExtra("groupObj", groupObj)
            intent.putExtra("transaction", trans)
            startActivity(intent)
        }

        addPaymentsBtn.setOnClickListener{
            val intent = Intent(this, RegisterNewPaymentActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        back_arrow.setOnClickListener{
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        groupStatsBtn.setOnClickListener {
            val intent = Intent(this, GroupStatsActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        bin_icon.setOnClickListener{

            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Confermi di volere eliminare il gruppo?")
                .setCancelable(false)
                .setPositiveButton("SI", DialogInterface.OnClickListener { dialog, id ->
                    FirebaseDBHelper.deleteGroup(passed_group_name)
                    val intent = Intent(this, SummaryActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("Elimina gruppo")
            alert.show()
        }
    }

    fun compute(){
        statsToSave = computeStatistics(listTransactions)
        saldiToSave = computeComeSaldare(statsToSave)
        FirebaseDBHelper.saveStatistics(passed_group_name, statsToSave)
        FirebaseDBHelper.saveComeSaldare(passed_group_name, saldiToSave)
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

    private fun getGroupsEventListener(): ChildEventListener {

        val listener = object : ChildEventListener{

            override fun onChildAdded(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if(item?.getGroupName() == passed_group_name) {

                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for ((key, value) in transactions) {
                            val t = value as HashMap<String, Any>
                            val trans = Transaction(
                                t["titolo"] as String,
                                t["pagatoDa"] as ArrayList<String>,
                                t["pagatoPer"] as ArrayList<String>,
                                t["totale"].toString().toDouble()
                            )
                            listTransactions.add(trans)
                        }
                        lv_spese_adapter.notifyDataSetChanged()
                        compute()
                    }
                }
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item?.getGroupName() == passed_group_name) {

                    val tempList = ArrayList<Transaction>()

                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["titolo"] as String,
                                t["pagatoDa"] as ArrayList<String>,
                                t["pagatoPer"] as ArrayList<String>,
                                t["totale"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }
                    }
                    listTransactions = tempList
                    lv_spese_adapter.notifyDataSetChanged()
                    compute()
                }
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)

                if (item?.getGroupName() == passed_group_name) {

                    val tempList = ArrayList<Transaction>()

                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {

                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["titolo"] as String,
                                t["pagatoDa"] as ArrayList<String>,
                                t["pagatoPer"] as ArrayList<String>,
                                t["totale"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }
                        //TEST TEST TEST
                        listTransactions = tempList
                        compute()
                    }
                    //listTransactions = tempList
                    lv_spese_adapter.notifyDataSetChanged()
                    //compute()
                }
            }
            override fun onChildMoved(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)

                if (item?.getGroupName() == passed_group_name) {

                    val tempList = ArrayList<Transaction>()

                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {

                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["titolo"] as String,
                                t["pagatoDa"] as ArrayList<String>,
                                t["pagatoPer"] as ArrayList<String>,
                                t["totale"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }

                    }
                    listTransactions = tempList
                    lv_spese_adapter.notifyDataSetChanged()
                    compute()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        return listener
    }

    //calcola quanto ogni partecipante è in positivo o negativo e la sua spesa totale individuale
    private fun computeStatistics(listTransactions: MutableList<Transaction>): ArrayList<SingleMemberStat>{
        var data =  ArrayList<SingleMemberStat>()
        var tempStat = HashMap<String,Double>()
        var tempSingleAmount = HashMap<String,Double>()

        for(i in passed_group_members.indices){      //inizializzo hashmap con nomi partecipanti
            tempStat[passed_group_members[i]] = 0.0
            tempSingleAmount[passed_group_members[i]] = 0.0
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

        for(i in passed_group_members.indices){
            data.add(SingleMemberStat(passed_group_members[i], tempStat[passed_group_members[i]]!!, tempSingleAmount[passed_group_members[i]]!!))
        }
        Log.i("COMPUTE-STATS","dataToReturn--> $data")
        return data
    }

    //calcola come saldare i debiti attuali
    private fun computeComeSaldare(listDebt: MutableList<SingleMemberStat>): ArrayList<SingleMemberDebt>{
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
        Log.i("COMPUTE-DEBT","debtToReturn--> $debiti")
        return debiti
    }
}
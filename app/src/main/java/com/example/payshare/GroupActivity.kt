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

    private lateinit var lvPaymentsAdapter : TransactionsListAdapter
    private lateinit var lvPayments : ListView
    private lateinit var passedGroupName : String
    private lateinit var passedGroupMembers : ArrayList<String>
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
        passedGroupName = groupObj.getGroupName() //Modifiche delle scritte della view
        passedGroupMembers = groupObj.getGroupMembers()
        groupName.text = passedGroupName //rimpiazzo textview con il nome del gruppo

        lvPaymentsAdapter = TransactionsListAdapter(this, listTransactions)  //inizializzo la listview
        lvPayments = group_list_view
        lvPayments.adapter = lvPaymentsAdapter
        FirebaseDBHelper.setListeners(getGroupsEventListener());

        lvPayments.setOnItemLongClickListener { adapterView, view, position, l ->
            var trans = listTransactions[position]
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Confermi di volere eliminare la transazione?")
                .setCancelable(false)
                .setPositiveButton("SI") { dialog, id ->
                    listTransactions.remove(trans)
                    FirebaseDBHelper.deleteTransaction(
                        passedGroupName,
                        trans.getTitle(),
                        trans.getTotal()
                    )
                    lvPaymentsAdapter.notifyDataSetChanged()
                }
                .setNegativeButton("NO") { dialog, id ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Elimina transazione")
            alert.show()
            true
        }

        lvPayments.setOnItemClickListener{ lv_adapter,listViewItems, position, id ->
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
                .setPositiveButton("SI") { dialog, id ->
                    FirebaseDBHelper.deleteGroup(passedGroupName)
                    val intent = Intent(this, SummaryActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("NO") { dialog, id ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Elimina gruppo")
            alert.show()
        }
    }

    fun compute(){
        statsToSave = computeStatistics(listTransactions)
        saldiToSave = computeComeSaldare(statsToSave)
        FirebaseDBHelper.saveStatistics(passedGroupName, statsToSave)
        FirebaseDBHelper.saveHowToPay(passedGroupName, saldiToSave)
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
                if(item?.getGroupName() == passedGroupName) {
                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for ((key, value) in transactions) {
                            val t = value as HashMap<String, Any>
                            val trans = Transaction(
                                t["title"] as String,
                                t["payedBy"] as ArrayList<String>,
                                t["payedFor"] as ArrayList<String>,
                                t["total"].toString().toDouble()
                            )
                            listTransactions.add(trans)
                        }
                        lvPaymentsAdapter.notifyDataSetChanged()
                        compute()
                    }
                }
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item?.getGroupName() == passedGroupName) {
                    val tempList = ArrayList<Transaction>()
                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["title"] as String,
                                t["payedBy"] as ArrayList<String>,
                                t["payedFor"] as ArrayList<String>,
                                t["total"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }
                    }
                    listTransactions = tempList
                    lvPaymentsAdapter.notifyDataSetChanged()
                    compute()
                }
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                if (item?.getGroupName() == passedGroupName) {
                    val tempList = ArrayList<Transaction>()
                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["title"] as String,
                                t["payedBy"] as ArrayList<String>,
                                t["payedFor"] as ArrayList<String>,
                                t["total"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }
                        listTransactions = tempList
                    }
                    lvPaymentsAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item?.getGroupName() == passedGroupName) {
                    val tempList = ArrayList<Transaction>()
                    val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()
                    if (transactions != null) {
                        for((key,value) in transactions){
                            val t = value as HashMap<String,Any>
                            val trans = Transaction(
                                t["title"] as String,
                                t["payedBy"] as ArrayList<String>,
                                t["payedFor"] as ArrayList<String>,
                                t["total"].toString().toDouble()
                            )
                            tempList.add(trans)
                        }

                    }
                    listTransactions = tempList
                    lvPaymentsAdapter.notifyDataSetChanged()
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

        for(i in passedGroupMembers.indices){      //inizializzo hashmap con nomi partecipanti
            tempStat[passedGroupMembers[i]] = 0.0
            tempSingleAmount[passedGroupMembers[i]] = 0.0
        }

        for(transaction in listTransactions.indices){
            val splittedAmount = listTransactions[transaction].getTotal() / listTransactions[transaction].getPayedBy().size
            val splittedDebt = listTransactions[transaction].getTotal() / listTransactions[transaction].getPayedFor().size
            val pagatoDa = listTransactions[transaction].getPayedBy()
            val pagatoPer = listTransactions[transaction].getPayedFor()

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

        for(i in passedGroupMembers.indices){
            data.add(SingleMemberStat(passedGroupMembers[i], tempStat[passedGroupMembers[i]]!!, tempSingleAmount[passedGroupMembers[i]]!!))
        }
        Log.i("COMPUTE-STATS","dataToReturn--> $data")
        return data
    }

    //calcola come saldare i debiti attuali
    private fun computeComeSaldare(listDebt: MutableList<SingleMemberStat>): ArrayList<SingleMemberDebt>{

        Log.i("COMPUTE-SALDARE","--------> ${listDebt}")
        var debts = ArrayList<SingleMemberDebt>()          //variabile da ritornare
        var membersPos = ArrayList<SingleMemberStat>()       //lista dei membri in positivo
        var membersNeg = ArrayList<SingleMemberStat>()       //lista dei membri in negativo
        var iPos = 0                                        //counter per il while membri in positivo
        var iNeg = 0                                        //counter per il while membri in positivo

        //listDebt = HashMap di String = nome, Double = totale debito/credito
        for(i in listDebt.indices){
            val memberStat = SingleMemberStat(listDebt[i].getMemberName(), listDebt[i].getMemberAmount(), listDebt[i].getSingleMemberTotal())
            if(memberStat.getMemberAmount() > 0){             //se l'amount è positivo
                membersPos.add(memberStat)                     //aggiungo alla lista dei positivi
            } else {
                membersNeg.add(memberStat)                     //altrimenti aggiungo alla lista dei negativi
            }
        }

        membersPos.sortByDescending{ it.getMemberAmount() }  //ordino i membri positivi, da chi ha speso di più a chi ha speso di meno
        membersNeg.sortBy{ it.getMemberAmount() }            //li ordino dal più piccolo al più grande, avendo numeri negativi

        while(iPos < membersPos.size && iNeg < membersNeg.size){      //fichè i contatori puntano a regioni di memoria valide
            val p = membersPos[iPos]                                 //p è un SingleMemberStat con amount positivo
            val n = membersNeg[iNeg]                                 //n è un SingleMemberStat con amount negativo

            if(p.getMemberAmount() >= n.getMemberAmount().absoluteValue){           //se amount positivo >= amount negativo
                debts.add(SingleMemberDebt(p.getMemberName(), n.getMemberName(), n.getMemberAmount().absoluteValue))   //aggiungo a debiti un debito da pagare
                iNeg ++                                                             //aumento i negativi per andare sul prossimo dato che ho saldato il primo della lista
                p.setAmount(p.getMemberAmount() + n.getMemberAmount())              //setto il nuovo debito da saldare per il positivo togliendo l'amount del negativo
            } else {
                debts.add(SingleMemberDebt(p.getMemberName(), n.getMemberName(), p.getMemberAmount().absoluteValue))   //aggiungo a debiti un debito da pagare
                iPos ++                                                             //aumento i positivi perchè sicuramente ho saldato il debito
                membersNeg[iNeg].setAmount((n.getMemberAmount() + p.getMemberAmount())) //setto il nuovo debito da saldare per il negativo
            }
        }
        Log.i("COMPUTE-DEBT","debtToReturn--> $debts")
        return debts
    }
}
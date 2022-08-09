package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupActivity : AppCompatActivity() {

    private lateinit var lv_spese_adapter : TransactionsListAdapter
    private lateinit var listview_payments : ListView
    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private lateinit var groupChildListener: ChildEventListener
    private val groupList: MutableList<Group> = ArrayList() //LISTA DI GRUPPO CHE PUò DIVENTARE 1 SOLO !!!!
    private var listTransactions = arrayListOf<Transaction>() //Lista delle transazioni da mostrare in ListView
    private lateinit var passed_group_name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        supportActionBar?.hide() //Tolgo barra titolo app

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName() //Modifiche delle scritte della view
        groupName.text = groupObj.getGroupName() //rimpiazzo textview con il nome del gruppo

        //inizializzo la listview
        lv_spese_adapter = TransactionsListAdapter(this, listTransactions)
        listview_payments = group_list_view
        listview_payments.adapter = lv_spese_adapter
        FirebaseDBHelper.setListeners(getGroupsEventListener());
        lv_spese_adapter.notifyDataSetChanged()

        //AGGIUNGERE RIMOZIONE DELLA TRANSAZIONE CON LONG CLICK LISTENER
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
            //Cancello il gruppo
            //Apro finestra Toast per conferma eliminazione gruppo
            FirebaseDBHelper.deleteGroup(passed_group_name)
            //ritorno all'activity precedenete SE non annullo
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
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

    private fun getGroupsEventListener(): ChildEventListener {
        val adapter = lv_spese_adapter
        val listener = object : ChildEventListener{

            override fun onChildAdded(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                dataSnap.child("transactions")
                val transactions = dataSnap.child("transactions").getValue<HashMap<String,Any>>()

                if(item?.getGroupName() == passed_group_name) {
                    groupList.add(item)
                    //listTransactions da popolare con oggetti transaction
                    if (transactions != null) {
                        var i = 0
                        for ((key, value) in transactions) {
                            //listaSpese.add(i, value as HashMap<String,Any>)
                            val spesa = value as HashMap<String,Any>
                            val trans = Transaction(
                                spesa["titolo"] as String,
                                spesa["pagatoDa"] as ArrayList<String>,
                                spesa["pagatoPer"] as ArrayList<String>,
                                (spesa["totale"] as Long).toDouble()
                            )
                            listTransactions.add(trans)
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

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        return listener
    }
}
package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.SimpleAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group.*
import java.util.*
import kotlin.collections.HashMap

class GroupActivity : AppCompatActivity() {

    lateinit var lv_spese_adapter : TransactionsListAdapter
    private var listaSpese = arrayListOf<HashMap<String,Any>>()
    private lateinit var listview_payments : ListView

    //Aggiunto per il db in locale
    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private lateinit var groupChildListener: ChildEventListener
    private val groupList: MutableList<Group> = ArrayList()
    private var listData = arrayListOf<HashMap<String,Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val groupObj = intent.extras?.get("group_obj") as Group
        val transactionsList = groupObj.getGroupTransactions() //arraylist di transazioni

        //Leggo transazioni dal db e le salvo
        //val listaTransazioniDatabase = FirebaseDatabase.getInstance().reference.child("groups").child("${groupObj.getGroupName()}").child("transactions")
        Log.i("AAA-ListaSpese.TOSTRING", groupList.toString())
        for(i in groupList.indices){
            if(groupList[i].getGroupName().equals(groupObj.getGroupName())){
                //transactionsList = groupList[i].getGroupTransactions()
            }
        }
        /*
        for(i in groupList.indices){
            if(groupList[i].getGroupName().equals(groupObj.getGroupName())){
                var listPayments = HashMap<String,Any>()
                listPayments["group_name"] = groupList[i].getGroupName()
                listPayments["obj_transaction"] = groupList[i].getGroupTransactions()
                listaSpese.add(listPayments)
            }
        } */

        //Adapter per fragment spese
        lv_spese_adapter = TransactionsListAdapter(this,transactionsList)

        listview_payments = lv_payments
        listview_payments.adapter = lv_spese_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener());

        groupName.text = groupObj.getGroupName() //rimpiazzo textview con il nome del gruppo
        groupPartecipants.text = groupObj.getGroupMembersToString() //rimpiazzo texview con i partecipanti del gruppo

        //popolo la listaSpese con le transazioni del gruppo ricevuto
        /*
        for(i in transactionsList.indices){
            var listPayments = HashMap<String,Any>()
            listPayments["titolo_spesa"] = transactionsList[i].getTitolo()
            listPayments["obj_transaction"] = transactionsList[i]
            listaSpese.add(listPayments)
        }*/

        lv_spese_adapter.notifyDataSetChanged()

        Log.i("DATI_SPESE_LISTA------>", listaSpese.toString())

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
                if (item != null) {
                    groupList.add(item)
                    //Rappresentazione grafica dell'oggetto
                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupObj"] = item
                    listData.add(listobj)
                    //test stampa in aggiunta - FUNZIONA
                    Log.i("GRUPPO SALVATO ------>", item.toString())
                }
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {

                    for (i in groupList.indices) {
                        if(groupList[i].getGroupName().equals(item.getGroupName())) {
                            groupList[i] = item;
                            break
                        }
                    }
                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupObj"] = item

                    for (i in listData.indices) {
                        var obj = listData[i]["groupName"];
                        if(obj.toString().equals(item.getGroupName())) {
                            listData[i] = listobj;
                            break
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {

                    for (i in groupList.indices) {
                        if(groupList[i].getGroupName().equals(item.getGroupName())) {
                            groupList.removeAt(i)
                            break
                        }
                    }
                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupObj"] = item

                    for (i in listData.indices) {
                        var obj = listData[i]["groupName"];
                        if(obj.toString().equals(item.getGroupName())) {
                            listData.removeAt(i)
                            break
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
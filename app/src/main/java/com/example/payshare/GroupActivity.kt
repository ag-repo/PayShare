package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

    //ADDED
    private var listTransactions = arrayListOf<Transaction>()
    private lateinit var passed_group_name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        //Modifiche delle scritte della view
        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        groupName.text = groupObj.getGroupName() //rimpiazzo textview con il nome del gruppo
        groupPartecipants.text = groupObj.getGroupMembersToString() //rimpiazzo texview con i partecipanti del gruppo

        //inizializzo la listview
        lv_spese_adapter = TransactionsListAdapter(
            this,
            listTransactions
        )

        listview_payments = group_list_view
        listview_payments.adapter = lv_spese_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener());

        lv_spese_adapter.notifyDataSetChanged()

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

                Log.i("ITEM", item.toString())
                Log.i("ITEM", item?.getGroupTransactions().toString())
                if(item?.getGroupName() == passed_group_name) {
                    groupList.add(item)
                    listTransactions = item.getGroupTransactions()

                    Log.i("CHILDGROUPACT", listTransactions.toString())
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
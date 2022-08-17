package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group.view.*
import kotlinx.android.synthetic.main.activity_summary.*
import kotlinx.android.synthetic.main.summary_list_group_item_layout.*
import kotlinx.android.synthetic.main.summary_list_group_item_layout.view.*
import java.io.Serializable


class SummaryActivity : AppCompatActivity() {

    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    lateinit var lv_adapter : SimpleAdapter
    private lateinit var groupChildListener: ChildEventListener
    private lateinit var listViewItems : ListView
    private val groupList: MutableList<Group> = ArrayList()     //Lista dei gruppi salvati
    private var listData = arrayListOf<HashMap<String,Any>>()   //Lista gruppi da mostrare su ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        supportActionBar?.hide() //Tolgo barra titolo app

        lv_adapter = SimpleAdapter( //Inizializzo listview
            this,
            listData,
            R.layout.summary_list_group_item_layout,
            arrayOf("groupName", "groupDescr"),
            intArrayOf(R.id.tv_groupName, R.id.tv_groupDescr)
        )
        
        listViewItems = groupListView
        listViewItems.adapter = lv_adapter

        FirebaseDBHelper.setListeners(getGroupsEventListener())

        group_number.text = listData.size.toString()

        addGroupBtn.setOnClickListener{
            val intent = Intent(this, AddNewGroupActivity::class.java)
            startActivity(intent)
        }

        //listener per apertura del gruppo selezionato nella listview
        listViewItems.setOnItemClickListener{ lv_adapter,listViewItems, position, id ->
            var obj = listData[position]["groupName"] //nome del gruppo
            var groupObj = Group()
            for(i in groupList.indices){
                if(groupList[i].getGroupName().equals(obj)){
                    groupObj = groupList[i]
                }
            }
            val intent = Intent(this, GroupActivity::class.java)
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
        val adapter = lv_adapter
        
        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                Log.i("SUMMARY-childAdded","groupList--> $groupList")
                Log.i("SUMMARY-childAdded","listData--> $listData")
                if (item != null) {
                    groupList.add(item)                             //arraylist di group presi dal db
                    val listobj = HashMap<String,Any>()             //Rappresentazione grafica dell'oggetto
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescr()
                    listData.add(listobj)                           //Arraylist di Hashmap per la grafica
                }
                Log.i("SUMMARY-childAdded","groupList--> $groupList")
                Log.i("SUMMARY-childAdded","listData--> $listData")
                group_number.text = listData.size.toString()        //AGGIORNO SCRITTA
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {
                    /*
                    for (i in groupList.indices) {
                        if(groupList[i].getGroupName() == item.getGroupName()) {
                            groupList[i] = item
                            break
                        }
                    }
                     */
                    if(groupList.contains(item)){
                        groupList.remove(item)
                    }
                    Log.i("SUMMARY-childChanged","groupList--> $groupList")

                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescr()

                    if(listData.contains(listobj)){
                        listData.remove(listobj)
                    }
                    Log.i("SUMMARY-childChanged","listData--> $listData")
                    /*for (i in listData.indices) {
                        var obj = listData[i]["groupName"]
                        if(obj.toString() == item.getGroupName()) {
                            listData[i] = listobj
                            break
                        }
                    }
                     */
                }
                group_number.text = listData.size.toString()        //AGGIORNO SCRITTA
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {

                    /*for (i in groupList.indices) {
                        if(groupList[i].getGroupName().equals(item.getGroupName())) {
                            groupList.removeAt(i)
                            break
                        }
                    }*/
                    if(groupList.contains(item)){
                        groupList.remove(item)
                    }

                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescr()

                    if(listData.contains(listobj)){
                        listData.remove(listobj)
                    }

                    /*
                    for (i in listData.indices) {
                        var obj = listData[i]["groupName"]
                        if(obj.toString().equals(item.getGroupName())) {
                            listData.removeAt(i)
                            break
                        }
                    }
                     */
                }

                Log.i("SUMMARY-childRemoved","groupList--> $groupList")
                Log.i("SUMMARY-childRemoved","listData--> $listData")

                group_number.text = listData.size.toString() //AGGIORNO SCRITTA
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@SummaryActivity, "Failed to load comments.", Toast.LENGTH_SHORT).show()
            }
        }
        return listener
    }
}
package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_summary.*


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
                if(groupList[i].getGroupName() == obj){
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
        groupReference!!.removeEventListener(groupChildListener)
    }

    private fun getGroupsEventListener(): ChildEventListener {
        val adapter = lv_adapter
        
        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {
                    groupList.add(item)                             //arraylist di group presi dal db
                    val listobj = HashMap<String,Any>()             //Rappresentazione grafica dell'oggetto
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescription()
                    listData.add(listobj)                           //Arraylist di Hashmap per la grafica
                }
                group_number.text = listData.size.toString()        //AGGIORNO SCRITTA
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousGroupName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {
                    if(groupList.contains(item)){
                        groupList.remove(item)
                    }
                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescription()
                    if(listData.contains(listobj)){
                        listData.remove(listobj)
                    }
                }
                group_number.text = listData.size.toString()    //AGGIORNO SCRITTA
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                if (item != null) {
                    if(groupList.contains(item)){
                        groupList.remove(item)
                    }
                    val listobj = HashMap<String,Any>()
                    listobj["groupName"] = item.getGroupName()
                    listobj["groupDescr"] = item.getGroupDescription()
                    if(listData.contains(listobj)){
                        listData.remove(listobj)
                    }
                }
                group_number.text = listData.size.toString()    //AGGIORNO SCRITTA
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
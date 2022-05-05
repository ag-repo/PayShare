package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_summary.*

class SummaryActivity : AppCompatActivity() {

    private val groupList: MutableList<Group> = ArrayList()
    lateinit var lv_adapter : SimpleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        //val data: MutableList<Group> = ArrayList<Group>()
        val data = arrayListOf<HashMap<String,Any>>()
        //inizializzo dati listview
        for(i in 1..10){
            val arrayNomi = ArrayList<String>() //creo nomi
            for (i in 1..3){
                arrayNomi.add("Nome $i")
            }

            val item = HashMap<String,Any>()
            val group = Group("Gruppo $i", "Descrizione gruppo $i", arrayNomi)
            groupList.add(group)
            item["groupName"] = group.getGroupName()
            item["groupDescr"] = group.getGroupDescr()
            data.add(item)
        }

        //Inizializzo listview
        val adapter = SimpleAdapter(
            this,
            data,
            R.layout.summary_list_group_item_layout,
            arrayOf("groupName", "groupDescr"),
            intArrayOf(R.id.tv_groupName, R.id.tv_groupDescr)
        )
        lv_adapter = adapter
        groupListView.adapter = adapter

        //Leggo dati da firebase
        FirebaseDBHelper.readGroups(getGroupsEventListener())

        addPayments.setOnClickListener{
            val intent = Intent(this, RegisterNewPaymentActivity::class.java)
            startActivity(intent)
        }

        groupButton.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

        addGroupBtn.setOnClickListener{
            val intent = Intent(this, AddNewGroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getGroupsEventListener(): ChildEventListener {
        val adapter = lv_adapter
        val listener = object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val item = p0.getValue(Group::class.java)
                if (item != null) { groupList.add(item) }
                val groupIndex = groupList.indexOf(item)
                groupListView.setItemChecked(groupIndex, false)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val item = p0.getValue(Group::class.java)
                groupList.remove(item)
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        return listener
    }
}
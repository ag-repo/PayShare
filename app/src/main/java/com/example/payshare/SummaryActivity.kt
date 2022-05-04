package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.android.synthetic.main.activity_summary.*

class SummaryActivity : AppCompatActivity() {

    private val data: MutableList<Group> = ArrayList()
    lateinit var adapter: ArrayAdapter<Group>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        //Inizializzo listview
        val listview: ListView = groupListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_2, data)
        listview.adapter = adapter

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
        val listener = object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val item = p0.getValue(Group::class.java)
                if (item != null) { data.add(item) }
                val groupIndex = data.indexOf(item)
                groupListView.setItemChecked(groupIndex, false)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val item = p0.getValue(Group::class.java)
                data.remove(item)
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
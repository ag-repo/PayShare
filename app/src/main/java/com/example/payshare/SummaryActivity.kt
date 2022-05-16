package com.example.payshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.android.synthetic.main.activity_summary.*
import kotlinx.android.synthetic.main.activity_summary.addGroupBtn

class SummaryActivity : AppCompatActivity() {

    private val groupList: MutableList<Group> = ArrayList()
    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().getReference("groups")
    lateinit var lv_adapter : SimpleAdapter
    private lateinit var groupChildListener: ChildEventListener
    private lateinit var listViewItems : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        val data = arrayListOf<HashMap<String,Any>>()
        //Inizializzo listview
        lv_adapter = SimpleAdapter(
            this,
            data,
            R.layout.summary_list_group_item_layout,
            arrayOf("groupName", "groupDescr"),
            intArrayOf(R.id.tv_groupName, R.id.tv_groupDescr)
        )
        
        listViewItems = groupListView
        listViewItems.adapter = lv_adapter


        /*for(g in groupList){
            val group : Group = groupList.get(g)
            Log.i("GRUPPO SALVATO ------>", group.toString())
        }*/

        //Aggiungo lettura DB
        val groupListListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var group = snapshot.getValue() as Group
                val g = HashMap<String,Any>()
                groupList.add(group)
                g["groupName"] = group.getGroupName()
                g["groupDescr"] = group.getGroupDescr()

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            //groupReference.addListenerForSingleEventValue()

            /*
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
            }*/
        }

        //Leggo dati da firebase
        FirebaseDBHelper.readGroups(getGroupsEventListener())
        val refreshButton = refreshGroups
        //refreshButton.setOnClickListener(getAddGroupClickListener()) //addGroupBtn

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
                if (item != null) {
                    groupList.add(item)
                    //test stampa in aggiunta - FUNZIONA
                    Log.i("GRUPPO SALVATO ------>", item.toString())
                }
                //val groupIndex = groupList.indexOf(item)
                //groupListView.setItemChecked(groupIndex, false)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousGroupName: String?) {
                val newGroup = dataSnap.getValue(Group::class.java)
                val groupKey = dataSnap.key
                groupList.find{ e -> e.toString().equals(groupKey)}?.set(newGroup!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                //val group = dataSnap.getValue(Group::class.java)
                val groupKey = dataSnap.key
                val fu = groupList.find{ e -> e.toString().equals(groupKey)}
                groupList.remove(fu)
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
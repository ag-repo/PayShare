package com.example.payshare

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_group_stats.*
import java.security.SecureRandom

class GroupStatsActivity : AppCompatActivity() {

    private lateinit var lvStatsAdapter : SingleMemberStatsListAdapter
    private lateinit var lvDebtAdapter : SingleMemberDebtListAdapter
    private lateinit var lvStats : ListView
    private lateinit var lvDebt : ListView
    private lateinit var passedGroupName: String
    private lateinit var groupObj : Group
    private var membersToDisplay = ArrayList<String>()          //valori presi dal gruppo passato per intent
    private var statsToDisplay = mutableListOf<SingleMemberStat>()  //calcolate in base alle transazioni ricevute
    private var saldiToDisplay = mutableListOf<SingleMemberDebt>()  //calcola come saldare i debiti

    private var groupReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("groups")
    private lateinit var groupChildListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_stats)
        supportActionBar?.hide() //Tolgo barra nome app

        groupObj = intent.extras?.get("group_obj") as Group
        passedGroupName = groupObj.getGroupName()
        membersToDisplay = groupObj.getGroupMembers()
        group_stats_name.text = passedGroupName //rimpiazzo nome gruppo nella view

        lvStatsAdapter = SingleMemberStatsListAdapter(this,statsToDisplay)
        lvDebtAdapter = SingleMemberDebtListAdapter(this,saldiToDisplay)
        lvStats = lv_groupStatisticsListView
        lvDebt = lv_comePagare
        lvStats.adapter = lvStatsAdapter
        lvDebt.adapter = lvDebtAdapter

        FirebaseDBHelper.setListeners(getGroupsEventListener())

        lvStatsAdapter.notifyDataSetChanged()

        back_to_group.setOnClickListener{
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group_obj", groupObj)
            startActivity(intent)
        }

        lv_comePagare.setOnItemLongClickListener{ adapterView, view, position, l ->
            var saldo = saldiToDisplay[position]
            val secureRandom = SecureRandom()
            var newTransName : String = "R-" +
                    "${saldo.getWhoPay()}" +
                    "-to-${saldo.getWhoReceive()}" +
                    "-${saldo.getDebt().toInt()}" +
                    "-${secureRandom.nextInt(100)}"
            val dialogBuilder = AlertDialog.Builder(this)

            dialogBuilder.setMessage("Vuoi saldare il debito?")
                .setCancelable(false)
                .setPositiveButton("SI") { dialog, id ->
                    val debitTrans = Transaction(
                        newTransName,
                        arrayListOf(saldiToDisplay[position].getWhoPay()),
                        arrayListOf(saldiToDisplay[position].getWhoReceive()),
                        saldiToDisplay[position].getDebt()
                    )
                    FirebaseDBHelper.setNewPayment(passedGroupName, debitTrans)
                    val intent = Intent(this, GroupActivity::class.java)
                    intent.putExtra("group_obj", groupObj)
                    startActivity(intent)
                }
                .setNegativeButton("NO") { dialog, id ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("SALDA DEBITO")
            alert.show()
            true
        }

        iv_refresh_stats.setOnClickListener{
            Log.i("STATS-TO-DISPLAY", "INUTILE !!!!!!!!!!!")
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

    private fun getGroupsEventListener(): ChildEventListener{

        val listener = object : ChildEventListener{
            override fun onChildAdded(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName() == passedGroupName) {
                    val stats = dataSnap.child("stats").getValue<ArrayList<SingleMemberStat>>()
                    if(stats != null){
                        for(i in stats.indices){
                            if(!statsToDisplay.contains(stats[i])){
                                statsToDisplay.add(stats[i])
                            }
                        }
                    }
                    lvStatsAdapter.notifyDataSetChanged()

                    val saldi = dataSnap.child("saldi").getValue<ArrayList<SingleMemberDebt>>()
                    if(saldi != null){
                        for(i in saldi.indices){
                            if(saldi[i].getDebt() != 0.0){
                                saldiToDisplay.add(saldi[i])
                            }
                        }
                    }
                    lvDebtAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildChanged(dataSnap: DataSnapshot, previousChildName: String?) {
                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName() == passedGroupName) {

                    val stats = dataSnap.child("stats").getValue<MutableList<SingleMemberStat>>()
                    if (stats != null) {
                        statsToDisplay = stats
                        lvStatsAdapter.notifyDataSetChanged()
                    }

                    val saldi = dataSnap.child("saldi").getValue<MutableList<SingleMemberDebt>>()
                    if (saldi != null) {
                        for(i in saldi.indices){
                            if(saldi[i].getDebt() != 0.0){
                                saldiToDisplay.add(saldi[i])
                            }
                        }
                    }
                    lvDebtAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnap: DataSnapshot) {
                val item = dataSnap.getValue(Group::class.java)
                if (item!!.getGroupName() == passedGroupName) {

                    val stats = dataSnap.child("stats").getValue<MutableList<SingleMemberStat>>()
                    if (stats != null) {
                        statsToDisplay = stats
                    }
                    lvStatsAdapter.notifyDataSetChanged()

                    val saldi = dataSnap.child("saldi").getValue<MutableList<SingleMemberDebt>>()
                    if (saldi != null) {
                        for(i in saldi.indices){
                            if(saldi[i].getDebt() != 0.0){
                                saldiToDisplay.add(saldi[i])
                            }
                        }
                    }
                    lvDebtAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildMoved(dataSnap: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        return listener
    }
}
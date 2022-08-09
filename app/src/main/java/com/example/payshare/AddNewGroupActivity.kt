package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_group.*
import kotlinx.android.synthetic.main.members_list_add_group_layout.*


class AddNewGroupActivity : AppCompatActivity() {

    lateinit var memberAdapter : GroupMemberListAdapter
    var tempGroupMemberList =  arrayListOf("Io")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_group)

        //Tolgo barra
        supportActionBar?.hide()

        memberAdapter = GroupMemberListAdapter(this, tempGroupMemberList)
        groupMembersLayout.adapter = memberAdapter

        groupMembersLayout.setOnItemClickListener{lv_adapter,listViewItems, position, id ->
            val memb_name = tempGroupMemberList[position]
            tempGroupMemberList.remove(memb_name)
            Log.i("REMOVED->", memb_name+" ARRAY "+ tempGroupMemberList.toString())
            memberAdapter.notifyDataSetChanged()
        }

        //se nome non presente lo aggiungo, altrimenti Toast con notifica
        addMemberBtn.setOnClickListener{
            if(!tempGroupMemberList.contains(et_newMember.text.toString())){
                tempGroupMemberList.add(et_newMember.text.toString())
                //inserire autocancellazione della edit text quando inserisce membro
                et_newMember.text.clear()
                groupMembersLayout.invalidateViews()
            } else {
                Toast.makeText(this, "Nome già presente",Toast.LENGTH_LONG).show()
            }
        }

        //add the new group button
        addGroupBtn.setOnClickListener{
            //Creo oggetto gruppo e lo aggiungo su Firebase
            val newGroup = Group(et_nomeGruppo.text.toString(), et_descrizione.text.toString(), tempGroupMemberList, arrayListOf())

            //AGGIUNGO CONTROLLO SE GRUPPO GIA PRESENTE !!!!!!!!

            FirebaseDBHelper.setNewGroup(newGroup.getGroupName(), newGroup)
            //richiamo SummaryActivity
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        back_to_summary_btn.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
    }
}
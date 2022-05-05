package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_group.*


class AddNewGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_group)

        var tempGroupMemberList =  arrayListOf<String>("IO")
        val memberAdapter = GroupMemberListAdapter(this, tempGroupMemberList)
        groupMembersLayout.adapter = memberAdapter

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
            val newGroup = Group(et_nomeGruppo.text.toString(), et_descrizione.text.toString(), tempGroupMemberList)
            FirebaseDBHelper.setNewGroup(newGroup.getGroupName(), newGroup)
            //richiamo SummaryActivity
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_group.*


class AddNewGroupActivity : AppCompatActivity() {

    private var tempGroupMemberList =  arrayListOf<GroupMember>(GroupMember("IO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_group)

        val memberAdapter = GroupMemberListAdapter(this, tempGroupMemberList)
        groupMembersLayout.adapter = memberAdapter

        //se nome non presente lo aggiungo, altrimenti Toast con notifica
        addMemberBtn.setOnClickListener{
            if(!tempGroupMemberList.contains(GroupMember(et_newMember.text.toString()))){
                tempGroupMemberList.add(GroupMember(et_newMember.text.toString()))
                groupMembersLayout.invalidateViews()
            } else {
                //NON FUNZIONAAAAAAAAAAAAAAAAA
                Toast.makeText(this, "Nome già presente",Toast.LENGTH_LONG).show()
            }
        }

        addGroupBtn.setOnClickListener{
            val newGroup = Group(et_nomeGruppo.text.toString(), tempGroupMemberList)
            val intent = Intent(this, SummaryActivity::class.java)

            val bundle = Bundle()
            bundle.putString("group_name", et_nomeGruppo.text.toString())
            bundle.putSerializable("group_list", tempGroupMemberList)
            intent.putExtra("newgroup_bundle", bundle)

            startActivity(intent)
        }
    }
}
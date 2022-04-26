package com.example.payshare

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
                Toast.makeText(this, "Nome già presente",Toast.LENGTH_LONG).show()
            }
        }

        addGroupBtn.setOnClickListener{

        }
    }
}
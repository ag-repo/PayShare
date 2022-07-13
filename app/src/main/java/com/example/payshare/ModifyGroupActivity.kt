package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_modify_group.*

class ModifyGroupActivity : AppCompatActivity() {

    private lateinit var passed_group_name : String
    private lateinit var passed_group_members : ArrayList<String>
    private lateinit var lv_adapter : GroupMemberListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_group)

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()
        passed_group_members = groupObj.getGroupMembers()

        tv_g_name.hint = passed_group_name
        tv_g_descr.hint = groupObj.getGroupDescr()

        lv_adapter = GroupMemberListAdapter(this,groupObj.getGroupMembers())
        lv_modify_members.adapter = lv_adapter

        add_new_memb.setOnClickListener{
            if(!passed_group_members.contains(et_new_memb.text.toString())){
                passed_group_members.add(et_new_memb.text.toString())
            } else {
                Toast.makeText(this, "Nome già presente", Toast.LENGTH_LONG).show()
            }
            lv_adapter.notifyDataSetChanged()
        }
    }
}
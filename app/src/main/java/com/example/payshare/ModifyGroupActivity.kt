package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ModifyGroupActivity : AppCompatActivity() {

    private lateinit var passed_group_name : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_group)

        val groupObj = intent.extras?.get("group_obj") as Group
        passed_group_name = groupObj.getGroupName()


    }
}
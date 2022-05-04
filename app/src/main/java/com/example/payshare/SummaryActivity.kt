package com.example.payshare

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_summary.*

class SummaryActivity : AppCompatActivity() {

    private val groups = arrayListOf<Group>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

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

        val extras = intent.extras
        if (extras != null) {
            val intent = getIntent()
            val args = intent.getBundleExtra("newgroup_bundle")

            val newGroupName = args?.getString("group_name")
            val newGroupMembers = args?.getSerializable("newgroup_bundle")
            val newGroup = Group(newGroupName.toString(), newGroupMembers)
            groups.add(newGroup)
        }
    }
}
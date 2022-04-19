package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SummaryActivity : AppCompatActivity() {

    val summaryTopFrag = SummaryTopFragment()       //Top bar della summary Activity
    val summaryGroupFrag = SummaryGroupFragment()   //Group view dei gruppi dello user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
    }



}
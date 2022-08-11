package com.example.payshare

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.single_member_stats_list_layout.view.*
import kotlinx.android.synthetic.main.transactions_list_layout.view.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class SingleMemberStatsListAdapter(private val context: Context, private var statList: ArrayList<SingleMemberStat>): BaseAdapter() {
    override fun getCount(): Int {
        return statList.size
    }

    override fun getItem(position: Int): Any {
        return statList[position]
    }

    override fun getItemId(position: Int): Long {
        return (statList[position]).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var newView = convertView
        if(newView == null){
            newView = LayoutInflater.from(context).inflate(R.layout.single_member_stats_list_layout, parent, false)
        }
        //arrotondo a 2 decimali dopo la virgola
        Log.i("STATS-Adapter", "newList --> ${statList}")
        val memberAmount = (statList[position].getMemberAmount()*100.0).roundToInt()/100.0
        newView!!.tv_nomePartecipante?.text = statList[position].getMemberName()
        newView!!.tv_personal_amount?.text = statList[position].getSingleMemberTotal().toString()
        newView!!.tv_statsAmount?.text = memberAmount.toString()
        return newView
    }

}
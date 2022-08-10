package com.example.payshare

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.single_member_debt_list_layout.view.*
import kotlinx.android.synthetic.main.single_member_stats_list_layout.view.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class SingleMemberDebtListAdapter(val context: Context, val data: ArrayList<GroupStatsActivity.SingleMemberDebt>): BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return (data[position]).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var newView = convertView
        if(newView == null){
            newView = LayoutInflater.from(context).inflate(R.layout.single_member_debt_list_layout, parent, false)
        }
        //arrotondo a 2 decimali dopo la virgola
        //val memberAmount = (data[position].getDebito()*100.0).roundToInt()/100.0
        (newView as View)?.tv_chipaga?.text = data[position].getPagante()
        (newView)?.tv_chiriceve?.text = data[position].getRicevente()
        (newView)?.tv_debtAmount?.text = data[position].getDebito().absoluteValue.toString()

        return newView
    }

}
package com.example.payshare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.members_list_add_group_layout.view.*

class GroupMemberListAdapter (val context:Context, val data:ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return (data[position]).hashCode().toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var newView = convertView
        if(newView == null){
            newView = LayoutInflater.from(context).inflate(R.layout.members_list_add_group_layout, parent, false)
        }

        val group = data[position]
        (newView as View)?.member_name?.text = group

        //check se ci sono nuovi dati
        //notifyDataSetChanged()

        return newView
    }

}
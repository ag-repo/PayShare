package com.example.payshare

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.summary_list_group_item_layout.view.*

class GroupsListAdapter(val context: Context, val data: ArrayList<HashMap<String, Any>>): BaseAdapter() {
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
            newView = LayoutInflater.from(context).inflate(R.layout.summary_list_group_item_layout, parent, false)
        }
        Log.i("GroupListAdapter","--> $data")
        val group = data[position]
        (newView as View).tv_groupName?.text = group["groupName"].toString()
        newView.tv_groupDescr?.text = group["groupDescr"].toString()
        return newView
    }
}
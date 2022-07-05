package com.example.payshare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.transactions_list_layout.view.*

class TransactionsListAdapter(val context: Context, val data:ArrayList<Transaction>): BaseAdapter() {
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
            newView = LayoutInflater.from(context).inflate(R.layout.transactions_list_layout, parent, false)
        }

        val transaction = data[position]
        (newView as View)?.tv_titolo_spesa?.text = transaction.getTitolo()
        newView?.tv_pagato_da?.text = transaction.getPagatoDa().toString()
        newView?.tv_amount?.text = transaction.getTotale().toString()

        //check se ci sono nuovi dati
        //notifyDataSetChanged()

        return newView
    }

}
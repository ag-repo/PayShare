package com.example.payshare

import java.io.Serializable

data class Transaction(
    private var title: String,
    private var payedBy: ArrayList<String>,
    private var payedFor: ArrayList<String>,
    private var total: Double) : Serializable {

    constructor() : this("", arrayListOf(), arrayListOf(), 0.0,)

    fun set(transaction: Transaction){
        title = transaction.title
        payedBy = transaction.payedBy
        payedFor = transaction.payedFor
        total = transaction.total
    }

    fun getPagatoDaToString(): String {
        val builder = StringBuilder()
        for(i in payedBy.indices){
            if(i == payedBy.size-1){
                builder.append(payedBy[i])
            } else {
                builder.append(payedBy[i] + ", ")
            }
        }
        return builder.toString()
    }

    fun getPagatoPerToString(): String {
        val builder = StringBuilder()
        for(i in payedFor.indices){
            if(i == payedFor.size-1){
                builder.append(payedFor[i])
            } else {
                builder.append(payedFor[i] + ", ")
            }
        }
        return builder.toString()
    }

    fun getTitle():String{
        return this.title
    }

    fun getPayedBy():ArrayList<String>{
        return this.payedBy
    }

    fun getPayedFor():ArrayList<String>{
        return this.payedFor
    }

    fun getTotal():Double{
        return this.total
    }

}
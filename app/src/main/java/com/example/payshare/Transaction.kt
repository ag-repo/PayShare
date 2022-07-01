package com.example.payshare

import java.io.Serializable

data class Transaction(
    private var titolo: String,
    private var pagatoDa: String,
    private var pagatoPer: ArrayList<String>,
    private var totale: Double) : Serializable {

    constructor() : this("","", arrayListOf(), 0.0,)

    fun set(transaction: Transaction){
        titolo = transaction.titolo
        pagatoDa = transaction.pagatoDa
        pagatoPer = transaction.pagatoPer
        totale = transaction.totale
    }

    fun getTitolo():String{
        return this.titolo
    }

    fun getPagatoDa():String{
        return this.pagatoDa
    }

    fun getPagatoPer():ArrayList<String>{
        return this.pagatoPer
    }

    fun getTotale():Double{
        return this.totale
    }

}
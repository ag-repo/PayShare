package com.example.payshare

import java.io.Serializable

data class Transaction(
    private var titolo: String,
    private var pagatoDa: ArrayList<String>,
    private var pagatoPer: ArrayList<String>,
    private var totale: Double) : Serializable {

    constructor() : this("", arrayListOf(), arrayListOf(), 0.0,)

    fun set(transaction: Transaction){
        titolo = transaction.titolo
        pagatoDa = transaction.pagatoDa
        pagatoPer = transaction.pagatoPer
        totale = transaction.totale
    }

    fun getPagatoDaToString(): String {
        val builder = StringBuilder()
        for(i in pagatoDa.indices){
            if(i == pagatoDa.size-1){
                builder.append(pagatoDa[i])
            } else {
                builder.append(pagatoDa[i] + ", ")
            }
        }
        return builder.toString()
    }

    fun getPagatoPerToString(): String {
        val builder = StringBuilder()
        for(i in pagatoPer.indices){
            if(i == pagatoPer.size-1){
                builder.append(pagatoPer[i])
            } else {
                builder.append(pagatoPer[i] + ", ")
            }
        }
        return builder.toString()
    }

    fun getTitolo():String{
        return this.titolo
    }

    fun getPagatoDa():ArrayList<String>{
        return this.pagatoDa
    }

    fun getPagatoPer():ArrayList<String>{
        return this.pagatoPer
    }

    fun getTotale():Double{
        return this.totale
    }

}
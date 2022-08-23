package com.example.payshare

data class SingleMemberDebt(private var whoReceive: String, private var whoPay: String, private var debt: Double){

    constructor() : this("", "", 0.0)

    fun set(debt: SingleMemberDebt){
        whoReceive = debt.whoReceive
        whoPay = debt.whoPay
        this.debt = debt.debt
    }

    fun getWhoPay(): String{
        return this.whoPay
    }

    fun getWhoReceive(): String{
        return this.whoReceive
    }

    fun getDebt(): Double{
        return this.debt
    }
}

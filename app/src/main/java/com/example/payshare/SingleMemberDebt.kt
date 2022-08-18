package com.example.payshare

data class SingleMemberDebt(private var ricevente: String, private var pagante: String, private var debito: Double){

    constructor() : this("", "", 0.0)

    fun set(debt: SingleMemberDebt){
        ricevente = debt.ricevente
        pagante = debt.pagante
        debito = debt.debito
    }

    fun getWhoPay(): String{
        return this.pagante
    }

    fun getWhoReceive(): String{
        return this.ricevente
    }

    fun getDebt(): Double{
        return this.debito
    }
}

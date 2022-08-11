package com.example.payshare

data class SingleMemberStat(private var memberName: String, private var memberAmount: Double, private var singleMemberTotal: Double){

    constructor() : this("", 0.0, 0.0)

    fun set(stat: SingleMemberStat){
        memberName = stat.memberName
        memberAmount = stat.memberAmount
        singleMemberTotal = stat.singleMemberTotal
    }

    fun setAmount(amount: Double){
        this.memberAmount = amount
    }

    fun setName(name: String){
        this.memberName = name
    }

    fun setSingleMemberTotal(amount: Double){
        this.singleMemberTotal = amount
    }

    fun getMemberName(): String{
        return this.memberName
    }

    fun getMemberAmount(): Double{
        return this.memberAmount
    }

    fun getSingleMemberTotal(): Double{
        return this.singleMemberTotal
    }
}

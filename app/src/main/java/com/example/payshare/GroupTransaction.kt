package com.example.payshare

data class GroupTransaction(
    private var groupName: String,
    private var groupMembers: ArrayList<String>,
    private var groupTransactions: HashMap<String, ArrayList<Float>>){

    constructor() : this("", arrayListOf(), hashMapOf())

    fun set(item: GroupTransaction){
        groupName = item.groupName
        groupMembers = item.groupMembers
        groupTransactions = item.groupTransactions
    }

    fun getGroupName(): String {
        return this.groupName
    }

    fun getGroupMembers(): ArrayList<String> {
        return this.groupMembers
    }

    fun getGroupTransactions(): HashMap<String,ArrayList<Float>> {
        return this.groupTransactions
    }
}

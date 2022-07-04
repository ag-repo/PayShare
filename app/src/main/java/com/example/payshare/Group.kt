package com.example.payshare

import java.io.Serializable

data class Group(
    private var groupName: String,
    private var groupDescr: String,
    private var groupMembers: ArrayList<String>,
    private var transactions: ArrayList<Transaction>) : Serializable{

    constructor() : this("", "", arrayListOf(), arrayListOf())

    override fun toString(): String {
        return groupName + " - " + groupDescr + " num partecipanti: " + groupMembers.size + " num transazioni: " + transactions.size
    }

    fun set(item: Group){
        groupName = item.groupName
        groupDescr = item.groupDescr
        groupMembers = item.groupMembers
        transactions = item.transactions
    }

    fun getGroupName(): String {
        return this.groupName
    }

    fun getGroupDescr(): String {
        return this.groupDescr
    }

    fun getGroupMembers(): ArrayList<String> {
        return this.groupMembers
    }

    fun getGroupMembersToString(): String {
        val builder = StringBuilder()
        for(i in groupMembers.indices){
            builder.append(groupMembers[i] + ", ")
        }
        return builder.toString()
    }

    fun getGroupTransactions(): ArrayList<Transaction>{
        return this.transactions
    }

    fun addNewTransaction(transaction: Transaction){
        transactions.add(transaction)
    }
}
package com.example.payshare

import java.io.Serializable

data class Group(
    private var groupName: String,
    private var groupDescr: String,
    private var groupMembers: ArrayList<String>) : Serializable{

    constructor() : this("", "", arrayListOf())

    override fun toString(): String {
        return groupName + " - " + groupDescr + " num partecipanti: " + groupMembers.size
    }

    fun set(item: Group){
        groupName = item.groupName
        groupDescr = item.groupDescr
        groupMembers = item.groupMembers
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
}
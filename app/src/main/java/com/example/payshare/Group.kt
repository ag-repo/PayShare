package com.example.payshare

import java.io.Serializable

data class Group(
    private var groupName: String,
    private var groupDescription: String,
    private var groupMembers: ArrayList<String>) : Serializable{

    constructor() : this("", "", arrayListOf())

    override fun toString(): String {
        return groupName + " - " + groupDescription + " num partecipanti: " + groupMembers.size
    }

    fun set(item: Group){
        groupName = item.groupName
        groupDescription = item.groupDescription
        groupMembers = item.groupMembers
    }

    fun getGroupName(): String {
        return this.groupName
    }

    fun getGroupDescription(): String {
        return this.groupDescription
    }

    fun getGroupMembers(): ArrayList<String> {
        return this.groupMembers
    }
}
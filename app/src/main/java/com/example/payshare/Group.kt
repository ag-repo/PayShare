package com.example.payshare

data class Group(
    private var groupName: String,
    private var groupMembers: ArrayList<String>) {

    constructor() : this("", arrayListOf("IO"))

    override fun toString(): String {
        return groupName
    }

    fun set(item: Group){
        groupName = item.groupName
        groupMembers = item.groupMembers
    }

    fun getGroupName(): String {
        return this.groupName
    }

    fun getGroupMembers(): ArrayList<String> {
        return this.groupMembers
    }
}
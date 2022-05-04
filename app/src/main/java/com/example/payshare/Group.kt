package com.example.payshare

import android.os.Parcel
import android.os.Parcelable

class Group(var groupName: String?, var groupMembers: ArrayList<String>) : Parcelable {

    companion object{
        val CREATOR = object : Parcelable.Creator<Group> {
            override fun createFromParcel(parcel: Parcel) = Group(parcel)
            override fun newArray(size: Int): Array<Group?> = arrayOfNulls(size)
            //override fun newArray(size: Int) = arrayListOf<String>().size
        }
    }

    /*

     */
    constructor(parcel: Parcel) : this(
        groupName = parcel.readString(),
        groupMembers = parcel.readArrayList()
    )

        /*
        constructor(parcel: Parcel) : this(mutableListOf<String>()) {
        parcel.readStringList(mStringList)
        } */

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(groupName)
    }

    override fun describeContents(): Int {
        return 0
    }
    /*
    companion object CREATOR : Parcelable.Creator<Group> {
        override fun createFromParcel(parcel: Parcel): Group {
            return Group(parcel)
        }

        override fun newArray(size: Int): Array<String?> {
            return arrayOfNulls(size)
        }
    }*/

}
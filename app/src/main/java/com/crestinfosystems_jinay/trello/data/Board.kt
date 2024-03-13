package com.crestinfosystems_jinay.trello.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.parcelize.Parceler

@Parcelize
data class Board(
    val name: String = "",
    val des: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
    ) {
    }


    companion object : Parceler<Board> {
        fun toObj(data: Map<String, Any>): Board {
            return Board(
                name = data["name"].toString(),
                des = data["des"].toString(),
                createdBy = data["createdBy"].toString(),
                assignedTo = data["assignedTo"] as ArrayList<String>
            )
        }

        override fun Board.write(parcel: Parcel, flags: Int) = with(parcel) {
            parcel.writeString(name)
            parcel.writeString(des)
            parcel.writeString(createdBy)
            writeStringList(assignedTo)
        }

        override fun create(parcel: Parcel): Board {
            return Board(parcel)
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf<String, Any>(
            "name" to name,
            "des" to des,
            "createdBy" to createdBy,
            "assignedTo" to assignedTo,
        )
    }
}

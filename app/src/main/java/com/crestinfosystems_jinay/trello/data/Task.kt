package com.crestinfosystems_jinay.trello.data

import android.os.Parcel
import android.os.Parcelable

data class Task(
    var title: String = "",
    var desc: String = "",
    var lastEdit: String = "",
    var state: State = State.todo,
    var key: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(State::class.java.classLoader) ?: State.done,
        parcel.readString() ?: ""
    )

    constructor(data: Map<String, Any>) : this(
        title = data["title"].toString() ?: "",
        desc = data["desc"].toString() ?: "",
        lastEdit = data["lastEdit"].toString() ?: "",
        state = State.valueOf(data["state"].toString()),
        key = data["key"].toString()
    )

    fun toMap(): Map<String, Any> {
        return mapOf<String, Any>(
            "title" to title,
            "desc" to desc,
            "lastEdit" to lastEdit,
            "state" to state.toString(),
            "key" to key,
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(desc)
        parcel.writeString(lastEdit)
        parcel.writeParcelable(state, flags)
        parcel.writeString(key)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}


enum class State : Parcelable {
    todo, doing, done;

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<State> {
        override fun createFromParcel(parcel: Parcel): State {
            return values()[parcel.readInt()]
        }

        override fun newArray(size: Int): Array<State?> {
            return arrayOfNulls(size)
        }
    }
}

package com.dandy.ugnius.dandy.model.entities

import android.os.Parcel
import android.os.Parcelable

class Track(
    var images: List<String>,
    val artists: String,
    val duration: Long,
    val explicit: Boolean,
    val id: String,
    val name: String,
    val uri: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt() == 1,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(images)
        parcel.writeString(artists)
        parcel.writeLong(duration)
        parcel.writeInt(if (explicit) 1 else 0)
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(uri)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }


    override fun equals(other: Any?) = (other as? Track)?.id == id

    override fun hashCode() = 31 * 17 + id.hashCode()
}
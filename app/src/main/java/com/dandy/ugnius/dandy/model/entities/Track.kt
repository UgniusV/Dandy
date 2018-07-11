package com.dandy.ugnius.dandy.model.entities

import android.os.Parcel
import android.os.Parcelable

class Track(
    var images: List<String>,
    val artists: String,
    val duration: String,
    val explicit: Boolean,
    val id: String,
    val name: String,
    val uri: String
) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt() == 1,
        parcel.readString(),
        parcel.readString(),
        parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(images)
        parcel.writeString(artists)
        parcel.writeString(duration)
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


    override fun equals(other: Any?): Boolean {
       return if (other is Track) {
            other.id == this.id && other.duration == this.duration
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = 17
        result = 31 * result + id.hashCode()
        result = 31 * result + duration.hashCode()
        return result
    }
}
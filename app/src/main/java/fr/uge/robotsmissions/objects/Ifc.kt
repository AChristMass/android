package fr.uge.robotsmissions.objects

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Ifc(
    var name: String = "",
    var last_upload: Date
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        Date(parcel.readLong())
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(last_upload.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ifc> {
        override fun createFromParcel(parcel: Parcel): Ifc {
            return Ifc(parcel)
        }

        override fun newArray(size: Int): Array<Ifc?> {
            return arrayOfNulls(size)
        }
    }
}
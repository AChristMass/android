package fr.uge.robotsmissions.objects

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Mission(
    var name: String = "",
    var ifc_target_name: String = "",
    var last_launched: Date
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(ifc_target_name)
        parcel.writeLong(last_launched.time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mission> {
        override fun createFromParcel(parcel: Parcel): Mission {
            return Mission(parcel)
        }

        override fun newArray(size: Int): Array<Mission?> {
            return arrayOfNulls(size)
        }
    }
}
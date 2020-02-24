package fr.uge.robotsmissions.objects

import android.os.Parcel
import android.os.Parcelable

data class Robot(
    var name: String = "",
    var ifc_position_name: String = "",
    var isOnline: Boolean = false,
    var battery: Float = 0.0F
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(ifc_position_name)
        parcel.writeByte(if (isOnline) 1 else 0)
        parcel.writeFloat(battery)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Robot> {
        override fun createFromParcel(parcel: Parcel): Robot {
            return Robot(parcel)
        }

        override fun newArray(size: Int): Array<Robot?> {
            return arrayOfNulls(size)
        }
    }


}
package com.example.my_bills.Model

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ismail")
data class Bills (
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    var name:String,
    var imageUri:ByteArray
){

}
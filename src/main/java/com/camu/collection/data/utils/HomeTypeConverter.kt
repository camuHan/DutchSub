package com.camu.collection.data.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import java.io.ByteArrayOutputStream

class HomeTypeConverter {
    @TypeConverter
    fun toByteArray(bitmap : Bitmap?) : ByteArray?{
        if(bitmap == null) {
            return null
        }
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // ByteArray -> Bitmap 변환
    @TypeConverter
    fun toBitmap(bytes : ByteArray?) : Bitmap?{
        if(bytes == null) {
            return null
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String = Gson().toJson(value)

    @TypeConverter
    fun toStringList(value: String) = Gson().fromJson(value, Array<String>::class.java).toList()
}
package com.camu.collection.data.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.camu.collection.domain.model.DutchMemberInfo
import com.camu.collection.domain.model.SubDutchInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.lang.reflect.Type
import javax.inject.Inject


@ProvidedTypeConverter
class HomeTypeConverter @Inject constructor(private val gson: Gson) {
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

    @TypeConverter
    fun dutchMembersToJson(values: List<DutchMemberInfo>): String? {
        val type: Type = object : TypeToken<List<DutchMemberInfo?>?>() {}.type
        return gson.toJson(values, type)
    }

    @TypeConverter
    fun jsonToDutchMembers(value: String): List<DutchMemberInfo> {
        val type: Type = object : TypeToken<List<DutchMemberInfo?>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun subDutchInfoToJson(values: List<SubDutchInfo>): String? {
        val type: Type = object : TypeToken<List<SubDutchInfo?>?>() {}.type
        return gson.toJson(values, type)
    }

    @TypeConverter
    fun jsonToSubDutchInfo(value: String): List<SubDutchInfo> {
        val type: Type = object : TypeToken<List<SubDutchInfo?>?>() {}.type
        return gson.fromJson(value, type)
    }

//    @TypeConverter
//    fun dutchMemberInfoToJson(values: ArrayList<DutchMemberInfo>): String? {
//        val type: Type = object : TypeToken<List<DutchMemberInfo?>?>() {}.type
//        return gson.toJson(values, type)
//    }
//
//    @TypeConverter
//    fun jsonTodutchMemberInfo(value: String): ArrayList<DutchMemberInfo> {
//        val type: Type = object : TypeToken<List<DutchMemberInfo?>?>() {}.type
//        return gson.fromJson(value, type)
//    }
}
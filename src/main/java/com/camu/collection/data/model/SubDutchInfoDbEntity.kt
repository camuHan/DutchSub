package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "SubDutchInfo")
data class SubDutchInfoDbEntity(
    @PrimaryKey(autoGenerate = true)
    var subId: Long? = null,
    var parentId: Long? = null,
    var userId: String = "",
    var dutchId: String = "",
    var name: String = "",
    var title: String = "",
    var coast: String = "",
    var members: List<String> = ArrayList(),
    var receiptList: List<String> = ArrayList(),
    var location: String = ""
)

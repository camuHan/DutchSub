package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.camu.collection.domain.model.DutchMemberInfo
import java.io.Serializable

@Entity(tableName = "SubDutchInfo")
data class SubDutchInfoDbEntity(
    @PrimaryKey(autoGenerate = true)
    var subId: Long? = null,
    var parentId: Long? = null,
    var userId: String = "",
    var dutchId: String = "",
    var userName: String = "",
    var subTitle: String = "",
    var coast: String = "",
    var payer: String = "",
    var subDutchMembers: List<DutchMemberInfo> = ArrayList(),
    var receipt: String = "",
    var receiptList: List<String> = ArrayList(),
    var location: String = ""
)

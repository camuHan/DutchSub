package com.camu.collection.data.model

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "DutchMemberInfo")
data class DutchMemberInfoDbEntity(
    var isPayer: Boolean = false,
    var photoUrl: String = "",
    var memberName: String = "",
    var percentage: String = "100",
    var dividends: String = "0",
)

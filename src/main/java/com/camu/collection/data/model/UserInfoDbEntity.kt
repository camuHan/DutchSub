package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.camu.collection.domain.model.BlockUserInfo

@Entity(tableName = "UserInfo")
data class UserInfoDbEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var userId: String? = "",
    var name: String? = "",
    var email: String? = "",
    var password: String ?= "",
    var phoneNumber: String? = "",
    var photoUrl: String? = "",
    var timestampCreated: String? = "",
    var blockUserList: List<BlockUserInfo> = ArrayList(),
    var isUnknown: Boolean = false
)

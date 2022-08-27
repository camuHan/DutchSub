package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var isUnknown: Boolean = false
)

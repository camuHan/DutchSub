package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "DutchInfo")
data class DutchInfoDbEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var userId: String = "",
    var dutchId: String = "",
    var name: String = "",
    var title: String = "",
    var contents: String = "",
    var contentsList: List<String> = ArrayList(),
    var location: String = "",
    var createdTime: String = "",
    var modifiedTime: String = "",
    var photoUrl: String = "",
    var likeCount: Int = 0,
    var commentCount: Int = 0,
    var viewCount: Int = 0,
    var password: String = "",
    var locked: Boolean = false
)

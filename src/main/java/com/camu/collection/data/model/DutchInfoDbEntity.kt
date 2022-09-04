package com.camu.collection.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.camu.collection.domain.model.CircleInfo
import com.camu.collection.domain.model.DutchMemberInfo
import com.camu.collection.domain.model.SubDutchMemberInfo
import com.camu.collection.domain.model.SubDutchInfo

@Entity(tableName = "DutchInfo")
data class DutchInfoDbEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Long? = null,
    var userId: String = "",
    var dutchId: String = "",
    var userName: String = "",
    var title: String = "",
    var dutchMembers: List<DutchMemberInfo> = ArrayList(),
    var subDutchInfos: List<SubDutchInfo> = ArrayList(),
    var contents: String = "",
    var contentsList: List<String> = ArrayList(),
    var location: String = "",
    var partyTime: String = "",
    var createdTime: String = "",
    var modifiedTime: String = "",
    var photoUrl: String = "",
    var likeList: List<String> = ArrayList(),
    var commentCount: Int = 0,
    var viewCount: Int = 0,
    var password: String = "",
    var locked: Boolean = false
)

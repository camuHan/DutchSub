package com.camu.collection.data.remote

import com.camu.collection.data.model.DutchInfoDbEntity
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun uploadProfileImage(uri: String)

}
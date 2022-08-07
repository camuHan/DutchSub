package com.camu.collection.data.local.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.camu.collection.data.model.DutchInfoDbEntity
import com.camu.collection.data.model.SubDutchInfoDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeLocalDutchDAO {
    @Insert(onConflict = REPLACE)
    suspend fun insert(dutchInfo: DutchInfoDbEntity)

    @Query("SELECT * FROM DutchInfo")
    fun getAll(): Flow<List<DutchInfoDbEntity>>

    @Query("SELECT * FROM DutchInfo WHERE dutchId = :dutchId")
    suspend fun getItem(dutchId: String): DutchInfoDbEntity

    @Query("SELECT * FROM DutchInfo WHERE dutchId LIKE :dutchId || '%'")
    suspend fun getItems(dutchId: String): List<DutchInfoDbEntity>

    @Query("DELETE FROM DutchInfo WHERE dutchId = :dutchId")
    suspend fun delete(dutchId: String)

    @Query("DELETE FROM DutchInfo WHERE dutchId LIKE :dutchId || '%'")
    suspend fun deleteFolder(dutchId: String)

    @Update
    suspend fun update(dutchInfo: DutchInfoDbEntity)

    @Delete
    suspend fun delete(dutchInfo: DutchInfoDbEntity)

    @Query("SELECT * FROM DutchInfo WHERE title LIKE '%' || :keyword || '%'")
    suspend fun searchItems(keyword: String): List<DutchInfoDbEntity>


//    @Insert(onConflict = REPLACE)
//    fun insert(file: SubDutchInfoDbEntity)
//
//    @Update
//    fun update(file: SubDutchInfoDbEntity)
//
//    @Delete
//    fun delete(file: SubDutchInfoDbEntity)

//    @Query("SELECT * FROM SubDutchInfo WHERE subId = :subId")
//    fun getSubItem(subId: String): SubDutchInfoDbEntity
//
//    @Query("SELECT * FROM SubDutchInfo WHERE parentId LIKE :parentId || '%'")
//    fun getSubItems(parentId: String): List<SubDutchInfoDbEntity>
}
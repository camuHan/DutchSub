package com.camu.collection.data.db

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.camu.collection.data.model.DutchInfoDbEntity

@Dao
interface HomeLocalDutchDAO {
    @Insert(onConflict = REPLACE)
    fun insert(file: DutchInfoDbEntity)

    @Query("SELECT * FROM DutchInfo")
    fun getAll(): List<DutchInfoDbEntity>

    @Query("SELECT * FROM DutchInfo WHERE dutchId = :dutchId")
    fun getItem(dutchId: String): DutchInfoDbEntity

    @Query("SELECT * FROM DutchInfo WHERE dutchId LIKE :dutchId || '%'")
    fun getItems(dutchId: String): List<DutchInfoDbEntity>

    @Query("DELETE FROM DutchInfo WHERE dutchId = :dutchId")
    fun delete(dutchId: String)

    @Query("DELETE FROM DutchInfo WHERE dutchId LIKE :dutchId || '%'")
    fun deleteFolder(dutchId: String)

    @Update
    fun update(file: DutchInfoDbEntity)

//    @Query("UPDATE DustInfo SET path = `REPLACE`(path, :path, :destPath) WHERE path LIKE :path || '%'")
//    fun move(path: String, destPath: String)    // room bug

    @Delete
    fun delete(file: DutchInfoDbEntity)

    @Query("SELECT * FROM DutchInfo WHERE title LIKE '%' || :keyword || '%'")
    fun searchItems(keyword: String): List<DutchInfoDbEntity>
}
package com.example.moodtrackr.db.router

import androidx.room.*

@Dao
interface RouterRequestsDAO {
    @Query("SELECT * FROM mt_router_requests mt WHERE mt.date >= :start AND mt.date <= :end")
    suspend fun getInTimeRange(start: Long, end: Long): List<RouterRequest>?

    @Query("SELECT * FROM mt_router_requests mt LIMIT 1")
    suspend fun getOne(): RouterRequest?

    @Query("SELECT * FROM mt_router_requests mt WHERE mt.date = :time LIMIT 1")
    suspend fun getObjOnTime(time: Long): RouterRequest?

    @Query("SELECT * FROM mt_router_requests mt")
    suspend fun getAll(): List<RouterRequest>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg usageRecord: RouterRequest)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(vararg usageRecord: RouterRequest)

    @Delete
    suspend fun delete(vararg usageRecord: RouterRequest)
}
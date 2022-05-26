package com.example.moodtrackr

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UnlockRecordDAO {

    @Query("SELECT * FROM unlockRecord WHERE time >= :start AND time <= :end")
    fun getAllInTimeRange(start: Long, end: Long): List<UnlockRecord>

    @Insert
    fun insertAll(vararg unlockRecord: UnlockRecord)

}
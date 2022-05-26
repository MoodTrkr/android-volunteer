package com.example.moodtrackr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UnlockRecord constructor(
    @PrimaryKey var time: Long
)
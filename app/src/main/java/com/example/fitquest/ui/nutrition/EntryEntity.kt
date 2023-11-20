// EntryEntity.kt
package com.example.fitquest.ui.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entry_table")
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val textString: String,
    val textNumber: Double
)
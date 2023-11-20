// EntryDao.kt
package com.example.fitquest.ui.nutrition

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addEntry(entry: EntryEntity)

    @Query("SELECT * FROM entry_table")
    fun getAllEntries(): LiveData<List<EntryEntity>>
}

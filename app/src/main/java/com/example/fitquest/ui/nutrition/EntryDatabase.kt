import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitquest.ui.nutrition.EntryDao
import com.example.fitquest.ui.nutrition.RowEntry

@Database(entities = [RowEntry::class], version = 1, exportSchema = false)
abstract class EntryDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao // You need to define an EntryDao interface

    // You can use a companion object to get a reference to the database
    companion object {
        // Singleton prevents multiple instances of the database opening at the same time.
        @Volatile
        private var INSTANCE: EntryDatabase? = null

        fun getDatabase(context: Context): EntryDatabase {
            // If the INSTANCE is not null, then return it; if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EntryDatabase::class.java,
                    "entry_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class,
        Product::class,
        Order::class,
        OrderItem::class,
        Payment::class,
        MandiRate::class,
        GovernmentScheme::class,
        SupportTicket::class,
        Notification::class,
        AuditLog::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KisanDatabase : RoomDatabase() {

    abstract fun kisanDao(): KisanDao

    companion object {
        @Volatile
        private var INSTANCE: KisanDatabase? = null

        fun getDatabase(context: Context): KisanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KisanDatabase::class.java,
                    "kisanbuy_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

package com.martodosko.studio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.martodosko.studio.data.local.dao.UserDao
import com.martodosko.studio.data.local.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

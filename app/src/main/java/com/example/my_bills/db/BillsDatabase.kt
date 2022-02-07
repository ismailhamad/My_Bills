package com.example.my_bills.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.my_bills.Model.Bills


@Database(
    entities = [Bills::class],version =1
)
 abstract class BillsDatabase: RoomDatabase() {
    abstract fun getdiarydao():BillsDAO
    companion object{
        @Volatile
        private var instance:BillsDatabase?=null
        private val LOCK=Any()
        operator fun invoke(context: Context)= instance?: synchronized(LOCK){
            instance?:createDatabase(context).also{ instance=it}
        }
        private fun createDatabase(context: Context)=
            Room.databaseBuilder(context.applicationContext,BillsDatabase::class.java,
                "Bills").allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}
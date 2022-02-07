package com.example.my_bills.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.my_bills.Model.Bills

@Dao
interface BillsDAO {
   @Insert
   fun insertBills(Bills:Bills)
    @Query("SELECT * FROM ismail")
    fun getallnote(): List<Bills>
    @Delete
   fun delete(Bills:Bills)
}
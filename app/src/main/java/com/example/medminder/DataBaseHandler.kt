package com.example.medminder

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

val DATABASE_NAME = "MyDB.db"
val TABLE_NAME = "MedInfo"
val COL_MEDNAME = "medName"
val COL_STARTDATE = "startDate"
val COL_TIME = "time"
val COL_FREQ = "freq"
val COL_TODAY = "today"
val COL_STATUS = "status"
val COL_ID = "id"

// ************************************
// DatabaseHandler helper class to access the SQLite Database and perform CRUD operations on it
// ************************************
class DataBaseHandler(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 2){
    val sdf = SimpleDateFormat("dd/M/yyyy")
    val currentDate = sdf.format(Date())

    // If Database with the name MyDB.db is not present, create it.
    // If Table by name "MedInfo" is not present, create it.
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "( " +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MEDNAME  + " VARCHAR(256), " +
                COL_STARTDATE + " VARCHAR(256), " +
                COL_TIME + " LONG, " +
                COL_FREQ + " VARCHAR(256), " +
                COL_TODAY + " VARCHAR(256), " +
                COL_STATUS + " BOOLEAN" +
                 ")")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, db1: Int, db2: Int) {
        TODO("not implemented")
    }

    // Insert Row.  Take the input MedInfo object and create a row to represent the Med Info.
    fun insertData(medInfo: MedInfo): Long{
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_MEDNAME, medInfo.medName)
        cv.put(COL_STARTDATE, medInfo.startDate)
        cv.put(COL_TIME, medInfo.time)
        cv.put(COL_FREQ, medInfo.freq)
        cv.put(COL_TODAY, medInfo.today)
        cv.put(COL_STATUS, medInfo.status)
        var result = db.insert(TABLE_NAME, null, cv)
        if(result < 0)
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()

        return result
    }

    // Read the Database and return an ArrayList of MedInfo objects
    fun readData() : MutableList<MedInfo>{
        var medications : MutableList<MedInfo> = ArrayList()
        val db = this.readableDatabase
        // Select everything from the Database
        val query = "Select * from " + TABLE_NAME
        // Get cursor to the Database rows
        // Iterate through the rows, create MedInfo objects and add to medications ArrayList
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do{
                var medInfo = MedInfo()
                medInfo.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                medInfo.medName = result.getString(result.getColumnIndex(COL_MEDNAME))
                medInfo.startDate = result.getString(result.getColumnIndex(COL_STARTDATE))
                medInfo.time = result.getString(result.getColumnIndex(COL_TIME)).toLong()
                medInfo.freq = result.getString(result.getColumnIndex(COL_FREQ))
                medInfo.today = result.getString(result.getColumnIndex(COL_TODAY))
                medInfo.status = result.getString(result.getColumnIndex(COL_STATUS)).toBoolean()

                medications.add(medInfo)

            }while(result.moveToNext())
        }
        result.close()
        db.close()

        return medications
    }


    // Update the "Status" and "Today" columns for the given Row ID
    fun updateStatAndToday(id : String, stat : String, date: String) {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_STATUS, stat)
        cv.put(COL_TODAY, date)
        db.update(TABLE_NAME, cv,COL_ID + "=" + id, null)
        db.close()
    }

}

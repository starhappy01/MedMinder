package com.example.medminder

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleText = findViewById<TextView>(R.id.titleText)
        var context = this
        //context.deleteDatabase(DATABASE_NAME)

        // Get today's date
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        // Add the "Add Medicine" button and call addMedActivity upon clicking
        val addMedButton = findViewById<Button>(R.id.addMedButton)
        addMedButton.setOnClickListener {
            val intent = Intent(this, addMedActivity::class.java)
            startActivity(intent)
        }

        // Read the list of medicine reminders from the Database
        var db = DataBaseHandler(this)
        var data = db.readData()
        var value : String = ""

        // Status = False means medicine not taken (for the day)
        // Status = True means medicine has been taken (for the day)
        // If "today" column is NOT Today, set "today" to Today and set Status to False for all rows
        // We are recognizing that this is a new day and resetting all the reminders (Status = False)
        for(i in 0..(data.size-1)){
            if(!data.get(i).today.equals(currentDate)){
                db.updateStatAndToday(data.get(i).id.toString(), "FALSE", currentDate)
            }
        }

        // Find the list view and populate it with the meds that need to be taken for the day
        val listView = findViewById<ListView>(R.id.listView)
        var strList : MutableList<String> = ArrayList()

        // Add only the medicines that are not yet taken for the day (Status = false)
        val now = LocalDateTime.now(ZoneOffset.UTC)
        val milliseconds = now.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()




        for(i in 0..(data.size-1)){
            var hours : Int = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(data.get(i).time), ZoneOffset.UTC).hour.toInt() + 4
            var minutes : Int = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(data.get(i).time), ZoneOffset.UTC).minute.toInt()

            if(data.get(i).status == false && (data.get(i).time.toInt() < milliseconds!!.toInt())){
                value += "                                  " + data.get(i).id + " " + data.get(i).medName.toUpperCase() + "   " + hours +  ":" + minutes
                strList.add(value)
                value = ""
            }
        }

        // Add the medicine list to the List View
        listView.adapter = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, strList)

        // User clicks the List Item once they finish taking the medicine for the day
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val listItem = parent.getItemAtPosition(position).toString()
            val parts : List<String> = listItem.split(" ")
            // Remove the medicine from the list view
            strList.removeAt(position)
            listView.adapter = ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strList)
            // Update the Medicine in the Database and set its State to True, indicating that the med has been taken (for the day)
            db.updateStatAndToday(parts[34], "TRUE", currentDate)

        }
    }
}



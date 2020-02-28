package com.example.medminder

import android.app.*
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.text.SimpleDateFormat
import java.util.*

//**********************************
// Class to handle Adding Medicine when "Add Medicine" button is clicked.
//**********************************
class addMedActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_med)

        val mRemText = findViewById<TextView>(R.id.mRemText)
        val mReminder : EditText = findViewById(R.id.mReminder)
        val mAddTimeButton = findViewById<Button>(R.id.mAddTimeButton)
        val mShowTime = findViewById<TextView>(R.id.mShowTime)
        val freqText = findViewById<TextView>(R.id.freqText)
        val radGroup : RadioGroup = findViewById(R.id.radGroup)
        val dailyButton : RadioButton = findViewById(R.id.dailyButton)
        val altDailyButton : RadioButton = findViewById(R.id.altDailyButton)
        val saveButton = findViewById<Button>(R.id.saveButton)


        var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

        var cal = Calendar.getInstance()

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        //Toast.makeText(this, currentDate, Toast.LENGTH_LONG).show()


        // Show the current time in Tine Chooser
        fun updateTimeInActivity(){
            val myFormat = "hh:mm a"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            //Toast.makeText(this, timeFormat.format(cal.getTime()), Toast.LENGTH_SHORT).show()
            mShowTime!!.text = sdf.format(cal.getTime())
        }


        //Once addTime button clicked, opens timepicker
        mAddTimeButton.setOnClickListener {
            var mTimeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.SECOND, 0)
                updateTimeInActivity()
            }
            TimePickerDialog(this, mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
        }

        // On Click Listener for Save button.
        saveButton.setOnClickListener {
            val radButtonId = radGroup.getCheckedRadioButtonId()
            val selectedRadBut : RadioButton = findViewById(radButtonId)

            val context = this

            // Get all the values from the View
            var medNameCol: String = mReminder.text.toString()
            var startDateCol: String = currentDate.toString()
            var timeCol: Long = cal.timeInMillis
            var freqCol: String = selectedRadBut.text.toString()
            var statusCol : Boolean = false

            // Create an instance of MedInfo object with all the data
            var medInfo = MedInfo(medNameCol, startDateCol, timeCol, freqCol, startDateCol, statusCol)
            // Get the Database handler and Create a new row and add the MedInfo data
            var db = DataBaseHandler(this)
            // Insert the new MedInfo and get the ID
            var insertedID = db.insertData(medInfo)
            db.close()

            //Create an alarm and notification for the Medicine Time
            // This will show a reminder notification on the device when it is time to take the medicine
            val mAlarmMan = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val broadcastIntent = Intent(this, AlarmBroadcastReceiver::class.java)
            broadcastIntent.putExtra("Text", mReminder.text.toString())

            val pIntent = PendingIntent.getBroadcast(this, insertedID.toInt(), broadcastIntent,0)

            var interval = AlarmManager.INTERVAL_DAY

            if(selectedRadBut.equals(altDailyButton)){
                interval = interval * 2
            }

            mAlarmMan?.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                interval,
                pIntent
            )
            val mesg : Toast = Toast.makeText(this, "Your medMinder is set for " + cal.time,  Toast.LENGTH_LONG)
            mesg.show()

            val intent5 = Intent(this, MainActivity::class.java)
            startActivity(intent5);
        }

    }
}

// *****************************
// Class to handle the Alarm Broadcast and set the Notification
// *****************************
class AlarmBroadcastReceiver : BroadcastReceiver() {

    var TAG = "CSProj"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {

        var cal = Calendar.getInstance()

        val reminderText = intent?.getStringExtra("Text")
        //Log.d(TAG, "Extracted text = " + reminderText)
        // Create the notification to be shown
        val mBuilder = NotificationCompat.Builder(context!!, "ONE")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(" Alarm App ")
            .setContentText(reminderText)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        val ch: NotificationChannel =
            NotificationChannel("ONE", "Ch ONE", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Channel ONE"
            }
        // Get the Notification manager service
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(ch)
        // Show a notification
        nm.notify(cal.get(Calendar.MINUTE), mBuilder.build())
    }
}


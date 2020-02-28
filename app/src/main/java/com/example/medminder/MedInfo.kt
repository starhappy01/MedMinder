package com.example.medminder

// Class MedInfo to capture the details of the Medicine
class MedInfo {

    var medName : String = "" // Name of the Medicine
    var startDate : String = ""
    var time : Long = 0 // Time to take the medicine
    var freq : String = "" // Frequency: "Daily" or "Alternate Days"
    var today : String = ""
    var status : Boolean = false // True = Medicine has been taken for the day.  False = not taken for the day
    var id : Int = 0

    constructor(medName:String, startDate: String, time: Long, freq: String, today: String, status: Boolean){
        this.medName = medName
        this.startDate = startDate
        this.time = time
        this.freq = freq
        this.today = today
        this.status = status
    }

    constructor(){
    }
}
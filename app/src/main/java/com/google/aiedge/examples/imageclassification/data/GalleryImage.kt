package com.google.aiedge.examples.imageclassification.data

import org.json.JSONObject
import java.util.*

class GalleryImage(
    val dateString: String,
    val timeString: String,
    val scanTypeString: String,
    val confidence: Double,
    val scanID: UUID,
    val patientName: String,
) {

    constructor(json: JSONObject) : this(
        dateString = json.getString("dateString"),
        timeString = json.getString("timeString"),
        scanTypeString = json.getString("scanTypeString"),
        confidence = json.getDouble("confidence"),
        scanID = UUID.fromString(json.getString("scanID")),
        patientName = json.getString("patientName")
    )


    fun toJSONObject() {
        JSONObject().apply {
            put("dateString", dateString)
            put("timeString", timeString)
            put("scanTypeString", scanTypeString)
            put("confidence", confidence)
            put("scanID", scanID.toString())
            put("patientName", patientName)
        }
    }
}
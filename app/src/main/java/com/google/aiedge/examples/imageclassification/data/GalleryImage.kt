package com.google.aiedge.examples.imageclassification.data

import org.json.JSONException
import org.json.JSONObject
import java.util.*
class GalleryImage(
    val dateString: String,
    val timeString: String,
    val scanTypeString: String,
    val label: String,
    val confidence: Double,
    val scanID: UUID,
    val patientName: String = "",
) {
    constructor(json: JSONObject) : this(
        dateString = json.optString("dateString", ""),
        timeString = json.optString("timeString", ""),
        scanTypeString = json.optString("scanTypeString", ""),
        label = json.optString("label", ""),
        confidence = json.optDouble("confidence", 0.0),
        scanID = try {
            UUID.fromString(json.optString("scanID"))
        } catch (e: Exception) {
            UUID.randomUUID()
        },
        patientName = json.optString("patientName", "")
    )

    fun toJSONObject(): JSONObject {
        return JSONObject().apply {
            put("dateString", dateString)
            put("timeString", timeString)
            put("scanTypeString", scanTypeString)
            put("label", label)
            put("confidence", confidence)
            put("scanID", scanID.toString())
            put("patientName", patientName)
        }
    }
}
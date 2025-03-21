package com.google.aiedge.examples.imageclassification.data

import org.json.JSONException
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
        dateString = json.optString("dateString").takeIf { it.isNotEmpty() }
            ?: throw JSONException("Missing or invalid 'dateString'"),
        timeString = json.optString("timeString").takeIf { it.isNotEmpty() }
            ?: throw JSONException("Missing or invalid 'timeString'"),
        scanTypeString = json.optString("scanTypeString").takeIf { it.isNotEmpty() }
            ?: throw JSONException("Missing or invalid 'scanTypeString'"),
        confidence = json.optDouble("confidence", Double.NaN).takeIf { it.isFinite() }
            ?: throw JSONException("Missing or invalid 'confidence'"),
        scanID = try {
            UUID.fromString(json.optString("scanID") ?: throw JSONException("Missing 'scanID'"))
        } catch (e: IllegalArgumentException) {
            throw JSONException("Invalid 'scanID' format")
        },
        patientName = json.optString("patientName").takeIf { it.isNotEmpty() }
            ?: throw JSONException("Missing or invalid 'patientName'")
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
package edu.singaporetech.hrapp.leave.record

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

/**
 * Repository for Firebase data to store the different field values
 */
@Parcelize
data class LeaveRecord(
    val id: String,
    var type: String,
    var from: String,
    var to: String,
    var doa: Timestamp,
    var daytype: String,
    var remarks: String,
    var file: String,
    var status: String,
    ) : Parcelable {
        companion object {
            fun DocumentSnapshot.toLeaveRecord(): LeaveRecord? {
                try {
                    val type = getString("type")!!
                    val from = getString("from")!!
                    val to = getString("to")!!
                    val doa = getTimestamp("doa")!!
                    val daytype = getString("daytype")!!
                    val remarks = getString("remarks")!!
                    val file = getString("file")!!
                    var status = getString("status")!!
                    return LeaveRecord(id, type, from, to, doa, daytype, remarks, file, status)
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting leave record", e)
                    FirebaseCrashlytics.getInstance().log("Error converting leave record")
                    FirebaseCrashlytics.getInstance().setCustomKey("leaveID", id)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    return null
                }
            }
            private const val TAG = "LeaveRecord"
        }
    }


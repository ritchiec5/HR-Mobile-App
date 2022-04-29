package edu.singaporetech.hrapp.leave.main

import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

/**
 * Repository for Firebase data to store the different field values
 */
@Parcelize
data class LeaveData(
    val id: String,
    var annual: Long,
    var annualleft: Long,
    var balance: Long,
    var compassion: Long,
    var compassionleft: Long,
    var others: Long,
    var othersleft: Long,
    var pending: Long,
    var sick: Long,
    var sickleft: Long,
    var total: Long,
    var used: Long,
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toLeaveData(): LeaveData? {

            try {
                val annual = getLong("annual")!!
                val annualused = getLong("annualleft")!!
                val balance = getLong("balance")!!
                val compassion = getLong("compassion")!!
                val compassionused = getLong("compassionleft")!!
                val others = getLong("others")!!
                val othersued = getLong("othersleft")!!
                val pending = getLong("pending")!!
                val sick = getLong("sick")!!
                val sickused = getLong("sickleft")!!
                val total = getLong("total")!!
                val used = getLong("used")!!
                return LeaveData(id, annual, annualused, balance, compassion, compassionused, others, othersued, pending, sick, sickused, total, used)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting leave data", e)
                FirebaseCrashlytics.getInstance().log("Error converting leave data")
                FirebaseCrashlytics.getInstance().setCustomKey("leaveDataID", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "LeaveData"
    }
}
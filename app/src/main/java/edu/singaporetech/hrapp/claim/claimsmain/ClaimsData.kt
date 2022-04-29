package edu.singaporetech.hrapp.claim.claimsmain
import android.os.Parcelable
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

/**
 * Repository for Claims data that is stored on firebase
 */
@Parcelize
data class ClaimsData(
    val id: String,
    var allowance: Long,
    var allowanceleft: Long,
    var approved: Long,
    var medical: Long,
    var medicalleft: Long,
    var others: Long,
    var othersleft: Long,
    var paid: Long,
    var pending: Long,
    var rejected: Long,
    var transport: Long,
    var transportleft: Long,
) : Parcelable {
    companion object {
        fun DocumentSnapshot.toClaimsData(): ClaimsData? {
            try {
                val allowance = getLong("allowance")!!
                val allowanceused = getLong("allowanceleft")!!
                val approved = getLong("approved")!!
                val medical = getLong("medical")!!
                val medicalused = getLong("medicalleft")!!
                val others = getLong("others")!!
                val otherused = getLong("othersleft")!!
                val paid = getLong("paid")!!
                val pending = getLong("pending")!!
                val rejected = getLong("rejected")!!
                val transport = getLong("transport")!!
                val transportused = getLong("transportleft")!!

                return ClaimsData(id, allowance, allowanceused, approved, medical, medicalused, others, otherused, paid,pending, rejected, transport, transportused,)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting claim data", e)
                FirebaseCrashlytics.getInstance().log("Error converting claim data")
                FirebaseCrashlytics.getInstance().setCustomKey("claimDataID", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "ClaimsData"
    }


}
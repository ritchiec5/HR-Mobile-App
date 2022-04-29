package edu.singaporetech.hrapp.claim.claimrecords
import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.DocumentSnapshot


/**
 * Repository for Claim records that is stored on firebase
 */
@Parcelize
data class Records (
    val id: String,
    val amount: Double,
    val category: String,
    val dop: Long,
    val status: String,
    val receiptno: String,
    val remarks: String,
    val uploadedfile: String) : Parcelable{
    companion object {
        fun DocumentSnapshot.toClaimRecord(): Records? {
            try {
                //Gets information of records
                val amount = getDouble("amount")!!
                val category = getString("category")!!
                val dop = getLong("dop")!!
                val receiptno = getString("receiptno")!!
                val remarks = getString("remarks")!!
                val status = getString("status")!!
                val uploadedfile = getString("uploadedfile")!!
                return Records(id, amount, category, dop, status, receiptno, remarks, uploadedfile)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting claim record", e)
                FirebaseCrashlytics.getInstance().log("Error converting claim record")
                FirebaseCrashlytics.getInstance().setCustomKey("userId", id)
                FirebaseCrashlytics.getInstance().recordException(e)
                return null
            }
        }
        private const val TAG = "User"
    }
    }
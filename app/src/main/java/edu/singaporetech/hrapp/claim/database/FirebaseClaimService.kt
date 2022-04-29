package edu.singaporetech.hrapp.claim.database

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firestore.v1.Target
import edu.singaporetech.hrapp.R
import edu.singaporetech.hrapp.claim.claimrecords.Records
import edu.singaporetech.hrapp.claim.claimrecords.Records.Companion.toClaimRecord
import edu.singaporetech.hrapp.claim.claimsmain.ClaimsData
import edu.singaporetech.hrapp.claim.claimsmain.ClaimsData.Companion.toClaimsData
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Service to get data from firebase
 */
object FirebaseClaimService {
    private const val TAG = "FirebaseClaimService"

    /**
     * Get Claims data from firebase
     * @return List<ClaimsData>
     */
    suspend fun getClaimsData(): List<ClaimsData> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("claimsData")
                .get()
                .await()
                .documents.mapNotNull { it.toClaimsData() }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting claims details", e)
            FirebaseCrashlytics.getInstance().log("Error getting claims records")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Get claims records for Claim Record Fragment
     * @return List<Records>
     */
    suspend fun getClaims(): List<Records> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("claim")
                .get().await()
                .documents.mapNotNull { it.toClaimRecord() }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting claim details", e)
            FirebaseCrashlytics.getInstance().log("Error getting user friends")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Function to Add new claims document in firebase
     * @param amount
     * @param category
     * @param dop
     * @param receiptno
     * @param remarks
     * @param ImageBase64
     */
    suspend fun addFireStoreClaims(amount: kotlin.Double, category: String, dop: Long, receiptno: String, remarks: String, ImageBase64: String){
        val db = FirebaseFirestore.getInstance()
        val claim: MutableMap<String,Any> = HashMap()

        claim["amount"] = amount
        claim["category"] = category
        claim["dop"] = dop
        claim["receiptno"] = receiptno
        claim["remarks"] = remarks
        claim["status"] = "Pending"
        claim["uploadedfile"] = ImageBase64

        db.collection("claim")
            .add(claim)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: \${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    /**
     * Function to Update & Edit claims
     * @param id
     * @param amount
     * @param category
     * @param dop
     * @param remarks
     * @param receiptno
     * @param uploadedfile
     */
    suspend fun updateClaim(id : String,
                            amount: Double,
                            category: String,
                            dop:Long,
                            remarks:String,
                            receiptno:String,
                            uploadedfile:String) {

        val db = FirebaseFirestore.getInstance()

        try {
            db.collection("claim").document(id)
                .update("amount", amount,
                "category", category,
                "dop",dop,
                "receiptno",receiptno,
                "remarks",remarks,
                "status","Pending",
                "uploadedfile",uploadedfile)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully update!")
                }.await()

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }

    }

    /**
     *  Function to delete claims in firebase by id
     *  @param id
     */
    suspend fun removeClaim(id : String) {
        val db = FirebaseFirestore.getInstance()
        val docRef : DocumentReference = db.collection("claim")
            .document(id)

        docRef.delete().addOnSuccessListener {
            Log.d(TAG, "Successfully deleted!")
        }.await()
    }

    /**
     * Function that Filters claim
     * @param status
     * @param category
     * @param startDateOfPurchase
     * @param endDateOfPurchase
     * @return List<Records>
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun filterClaim(
        status: String,
        category: String,
        startDateOfPurchase: String,
        endDateOfPurchase: String): List<Records> {

        val db = FirebaseFirestore.getInstance()

        Log.d("Test","status:${status} , category:${category} , start:${startDateOfPurchase} , end:${endDateOfPurchase}")
        return try {
            var ref : CollectionReference = db.collection("claim")
            var query : Query = ref

                //Filter via status
                if(status == "0"){
                    null
                }else {
                    query = query.whereEqualTo("status", status)
                }

                // Filters via category
                if(category == "0"){
                    null
                }else {
                    query = query.whereEqualTo("category", category)
                }

                // Filters via Date Range
                if(startDateOfPurchase == "0" && endDateOfPurchase == "0"){

                    null
                }else {
                    var startDTstring = startDateOfPurchase.dropLast(3)
                    var startDT = startDTstring.toLong()
                    var endDTstring = endDateOfPurchase.dropLast(3)
                    var endDT = endDTstring.toLong()
                    query = query.whereGreaterThanOrEqualTo("dop", startDT).whereLessThanOrEqualTo("dop", endDT)
                }

                query.get().await()
                .documents.mapNotNull { it.toClaimRecord() }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Function to Update & Edit Claim Data
     * @param amount
     * @param claimType
     */
    suspend fun updateClaimData(amount: Number,claimType: String) {

        val db = FirebaseFirestore.getInstance()
        val claimsDataList = getClaimsData()

        try {
            if (claimType == "Transport") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("transportleft", claimsDataList[0].transportleft.toInt() - amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (claimType == "Medical") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("medicalleft", claimsDataList[0].medicalleft.toInt() - amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (claimType == "Allowance") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("allowanceleft", claimsDataList[0].allowanceleft.toInt() - amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (claimType == "Others") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("othersleft", claimsDataList[0].othersleft.toInt() - amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                .update("pending", claimsDataList[0].pending.toInt()+1)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Function that Updates Claim Data upon cancellation of pending claims
     * @param amount
     * @param claimType
     */
    suspend fun refundClaimData(amount: Number,claimType: String) {

        val db = FirebaseFirestore.getInstance()
        val claimsDataList = getClaimsData()

        try {
            if (claimType == "Transport") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("transportleft", claimsDataList[0].transportleft.toInt() + amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            } else if (claimType == "Medical") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("medicalleft", claimsDataList[0].medicalleft.toInt() + amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            } else if (claimType == "Allowance") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update(
                        "allowanceleft",
                        claimsDataList[0].allowanceleft.toInt() + amount.toInt()
                    )
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            } else if (claimType == "Others") {
                db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                    .update("othersleft", claimsDataList[0].othersleft.toInt() + amount.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            db.collection("claimsData").document("ql8fozHXn2PHvuiDry9F")
                .update("pending", claimsDataList[0].pending.toInt() - 1)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}
package edu.singaporetech.hrapp.leave.database

import android.content.ContentValues
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.singaporetech.hrapp.leave.main.LeaveData
import edu.singaporetech.hrapp.leave.main.LeaveData.Companion.toLeaveData
import edu.singaporetech.hrapp.leave.record.LeaveRecord
import edu.singaporetech.hrapp.leave.record.LeaveRecord.Companion.toLeaveRecord
import kotlinx.coroutines.tasks.await

/**
 * Firebase Service to obtain document data
 */
object FirebaseLeaveService {
    private const val TAG = "FirebaseLeaveService"

    /**
     * Get leave records that are applied
     * @return List<LeaveRecord>
     */
    suspend fun getLeaves(): List<LeaveRecord> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("leaves")
                .get().await()
                .documents.mapNotNull { it.toLeaveRecord() }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting leave details", e)
            FirebaseCrashlytics.getInstance().log("Error getting leave records")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Gets the remaining leaves data amount
     * @return List<LeaveData>
     */
    suspend fun getLeavesData(): List<LeaveData> {
        val db = FirebaseFirestore.getInstance()
        return try {
            db.collection("leavesData")
                .get()
                .await()
                .documents.mapNotNull { it.toLeaveData() }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error getting leave details", e)
            FirebaseCrashlytics.getInstance().log("Error getting leave records")
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }

    /**
     * Adds a leave document to the firebase storage
     * @param leave
     */
    suspend fun addLeave(leave : MutableMap<String,Any>) {
        val db = FirebaseFirestore.getInstance()
        db.collection("leaves")
            .add(leave)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: \${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }.await()
    }

    /**
     * Deletion of leaves in the firebase
     * @param id
     */
    suspend fun removeLeave(id : String) {
        val db = FirebaseFirestore.getInstance()
        val docRef : DocumentReference = db.collection("leaves")
            .document(id)

        docRef.delete().addOnSuccessListener {
            Log.d(TAG, "Successfully deleted!")
        }.await()
    }

    /**
     * Update leavesData
     * @param days
     * @param leaveType
     */
    suspend fun updateLeave(days: Number,leaveType: String) {

        val db = FirebaseFirestore.getInstance()
        val leaveDataList = getLeavesData()

        try {
            if (leaveType == "Annual") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("annualleft", leaveDataList[0].annualleft.toInt() - days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Compassion") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("compassionleft", leaveDataList[0].compassionleft.toInt() - days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Sick") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("sickleft", leaveDataList[0].sickleft.toInt() - days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Others") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("othersleft", leaveDataList[0].othersleft.toInt() - days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                .update("balance", leaveDataList[0].balance.toInt() - days.toInt())
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }
            db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                .update("pending", leaveDataList[0].pending.toInt()+1)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Refunds leavesData amount upon cancellation
     * @param days
     * @param leaveType
     */
    suspend fun refundLeave(days: Number,leaveType: String) {

        val db = FirebaseFirestore.getInstance()
        val leaveDataList = getLeavesData()

        try {
            if (leaveType == "Annual") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("annualleft", leaveDataList[0].annualleft.toInt() + days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Compassion") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("compassionleft", leaveDataList[0].compassionleft.toInt() + days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Sick") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("sickleft", leaveDataList[0].sickleft.toInt() + days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            else if (leaveType == "Others") {
                db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                    .update("othersleft", leaveDataList[0].othersleft.toInt() + days.toInt())
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "Successfully update!")
                    }
            }
            db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                .update("balance", leaveDataList[0].balance.toInt() + days.toInt())
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }
            db.collection("leavesData").document("GqcHrS5aSqN3qLLrV9Vm")
                .update("pending", leaveDataList[0].pending.toInt()-1)
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Successfully update!")
                }

        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error updating claim details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Function to filter leaves based on type and status
     * @param type
     * @param status
     * @return List<LeaveRecord>
     */
    suspend fun filterLeave(
        type: String,
        status: String): List<LeaveRecord> {

        val db = FirebaseFirestore.getInstance()

        Log.d("Test","type:${type} , status:${status}")
        return try {
            var ref : CollectionReference = db.collection("leaves")
            var query : Query = ref

            //filter type
            if(type == "0"){
                null
            }else {
                query = query.whereEqualTo("type", type)
            }

            //filter status
            if(status == "0"){
                null
            }else {
                query = query.whereEqualTo("status", status)
            }

            query.get().await()
                .documents.mapNotNull { it.toLeaveRecord() }
        }catch (e:Exception) {
            Log.e(ContentValues.TAG, "Error getting leave details", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            emptyList()
        }
    }
}
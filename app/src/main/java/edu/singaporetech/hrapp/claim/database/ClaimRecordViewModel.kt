package edu.singaporetech.hrapp.claim.database

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.hrapp.claim.claimrecords.Records
import edu.singaporetech.hrapp.claim.claimsmain.ClaimsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Class for the claims record view model
 * @return ViewModel()
 */
class ClaimRecordViewModel : ViewModel(){
    private val claimDetail = MutableLiveData<List<Records>>()
    val getClaimRecord: LiveData<List<Records>> = claimDetail

    private val _claimsData = MutableLiveData<List<ClaimsData>>()

    val getClaimsData: LiveData<List<ClaimsData>> = _claimsData

    init {
        viewModelScope.launch() {
            claimDetail.value = FirebaseClaimService.getClaims()
        }
        viewModelScope.launch() {
            _claimsData.value = FirebaseClaimService.getClaimsData()
        }
    }

    /**
     * Update claim records function
     * @param id
     * @param amount
     * @param category
     * @param dop
     * @param remarks
     * @param receiptno
     * @param uploadedfile
     */
    fun updateClaim(id : String,
                    amount: Double,
                    category: String,
                    dop:Long,
                    remarks:String,
                    receiptno:String,
                    uploadedfile:String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseClaimService.updateClaim(id,amount,category,dop,remarks,receiptno,uploadedfile)
        }
    }

    /**
     * Add new claims function
     * @param amount
     * @param category
     * @param dop
     * @param remarks
     * @param receiptno
     * @param uploadedfile
     */
    fun addFireStoreClaims(
                    amount: Double,
                    category: String,
                    dop:Long,
                    remarks:String,
                    receiptno:String,
                    uploadedfile:String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseClaimService.addFireStoreClaims(amount,category,dop,remarks,receiptno,uploadedfile)
        }
    }

    /**
     * Delete claims function
     * @param id
     */
    fun removeClaim(id : String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseClaimService.removeClaim(id)
        }
    }

    /**
     * Update claims data function
     * @param amount
     * @param claimType
     */
    fun updateClaimData(amount: Number,claimType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseClaimService.updateClaimData(amount, claimType)
        }
    }

    /**
     * Refund claims data function
     * @param amount
     * @param claimType
     */
    fun refundClaimData(amount: Number,claimType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseClaimService.refundClaimData(amount, claimType)
        }
    }

    /**
     * Function that filters claims by status, category and date range
     * @param status
     * @param startDateOfPurchase
     * @param endDateOfPurchase
     * @return claimDetail
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterClaim(status: String, category: String, startDateOfPurchase: String, endDateOfPurchase: String) : LiveData<List<Records>>{
        claimDetail.value = arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {
            claimDetail.postValue(FirebaseClaimService.filterClaim(status, category, startDateOfPurchase, endDateOfPurchase))
        }
        return claimDetail
    }

}
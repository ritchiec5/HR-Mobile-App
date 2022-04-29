package edu.singaporetech.hrapp.leave.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.singaporetech.hrapp.leave.main.LeaveData
import edu.singaporetech.hrapp.leave.record.LeaveRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Class for the leave record viewmodel
 * @return ViewModel()
 */
class LeaveRecordViewModel : ViewModel() {

    private val _leaveDetail = MutableLiveData<List<LeaveRecord>>()
    private val _leaveData = MutableLiveData<List<LeaveData>>()

    val getleaveRecord: LiveData<List<LeaveRecord>> = _leaveDetail
    val getLeaveData: LiveData<List<LeaveData>> = _leaveData

    init {
        viewModelScope.launch() {
            _leaveDetail.value = FirebaseLeaveService.getLeaves()
        }

        viewModelScope.launch() {
            _leaveData.value = FirebaseLeaveService.getLeavesData()
        }
    }

    /**
     *Update leaves data function
     * @param days
     * @param leaveType
     */
    fun updateLeave(days: Number,leaveType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseLeaveService.updateLeave(days, leaveType)
        }
    }

    /**
     *Refund leaves data function
     * @param days
     * @param leaveType
     */
    fun refundLeave(days: Number,leaveType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseLeaveService.refundLeave(days, leaveType)
        }
    }

    /**
     *Removes an entry from the leave record
     * @param id
     */
    fun removeLeave(id : String) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseLeaveService.removeLeave(id)
        }
    }

    /**
     * Adds a leave document into firebase
     * @param leave
     */
    fun addLeave(leave : MutableMap<String,Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseLeaveService.addLeave(leave)
        }
    }

    /**
     *Filters leave based on category
     * @param status
     * @param category
     * @return _leaveDetail
     */
    fun filterLeave(status: String, category: String ) : LiveData<List<LeaveRecord>>{
        _leaveDetail.value = arrayListOf()
        viewModelScope.launch(Dispatchers.IO) {
            _leaveDetail.postValue(FirebaseLeaveService.filterLeave(status, category))
        }
        return _leaveDetail
    }
}
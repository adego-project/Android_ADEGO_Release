package com.seogaemo.android_adego.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seogaemo.android_adego.data.PlanStatus
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.util.Util.getPlan
import com.seogaemo.android_adego.view.main.MainActivity


class PlanViewModel : ViewModel() {

    private val _planStatus = MutableLiveData<PlanStatus>()
    val planStatus: LiveData<PlanStatus> get() = _planStatus

    private val _plan = MutableLiveData<PlanResponse>()
    val plan: LiveData<PlanResponse> get() = _plan

    private val _planDate = MutableLiveData<String>()
    val planDate: LiveData<String> get() = _planDate

    fun fetchPlan(activity: MainActivity) {
        viewModelScope.launch {
            try {
                val planResponse = getPlan(activity)
                val status = determinePlanStatus(planResponse)
                planResponse?.let { _plan.value = it }
                _planStatus.value = status
                if (status != PlanStatus.NO_PROMISE) _planDate.value = planResponse?.date
            } catch (e: Exception) {
                _planStatus.value = PlanStatus.NO_PROMISE
            }
        }
    }

    private fun determinePlanStatus(plan: PlanResponse?): PlanStatus {
        return if (plan == null) {
            PlanStatus.NO_PROMISE
        } else if (plan.isAlarmAvailable) {
            PlanStatus.ACTIVE
        } else {
            PlanStatus.DISABLED
        }
    }

    fun setPlanStatus(isNoPromise: Boolean) {
        if (isNoPromise) {
            _planStatus.value = PlanStatus.NO_PROMISE
        } else {
            _planStatus.value = PlanStatus.ACTIVE
        }
    }
}

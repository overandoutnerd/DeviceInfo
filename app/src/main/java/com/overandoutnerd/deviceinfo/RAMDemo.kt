package com.overandoutnerd.deviceinfo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RamDetails(
    var total: Long = 0,
    var used: Long = 0,
    var available: Long = 0
)
class RamViewModel(application: Application): AndroidViewModel(application) {
    
    private val _ramInfo = MutableLiveData<RamDetails>()
    val ramInfo: LiveData<RamDetails> = _ramInfo
    
    init {
        observeRamUsage(application)
    }
    
    private fun observeRamUsage(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                val ramUsage = updateRamUsage(context)
                _ramInfo.postValue(ramUsage)
                delay(1000)
            }
        }
    }
    
    private fun updateRamUsage(context: Context): RamDetails{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem
        val usedRam = totalRam - memoryInfo.availMem
        val availableRam = memoryInfo.availMem
        val percentage = (usedRam.toDouble()/totalRam.toDouble()) * 100
        return RamDetails(totalRam, usedRam, availableRam)
    }
    
}

@Composable
fun RamInfoScreen(viewModelStoreOwner: ViewModelStoreOwner){
    val viewModel = ViewModelProvider(viewModelStoreOwner).get(RamViewModel::class.java)
    val ramInfo by viewModel.ramInfo.observeAsState()
    

}
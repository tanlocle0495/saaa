package com.kabu.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kabu.myapplication.PreferenceHelper.sechule
import com.kabu.myapplication.PreferenceHelper.userInfo
import com.kabu.myapplication.api.RemoteDataSource
import com.kabu.myapplication.model.CreateTicketRequest
import com.kabu.myapplication.repository.NetworkResult
import com.kabu.myapplication.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar


class MainViewModelFactory(private val viewModel: ViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ApiService : Service() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent


    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        scheduleApiCall()
        return START_STICKY
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    private fun scheduleApiCall() {
        val currentTimeMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Tăng lên ngày kế tiếp
        calendar.set(Calendar.SECOND, 0)
        val delay = calendar.timeInMillis - currentTimeMillis
        coroutineScope.launch {
            delay((delay).toLong())
            print("âsasasasas");
            callApiAndStopService();
        }

    }

    private suspend fun callApiAndStopService() {
        val ref = PreferenceHelper.customPreference(context = this)
        repository.getTicketList(ref.userInfo.sub).collect { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        if (it.isNotEmpty()) {
                            PreferenceHelper.customPreference(context = this).sechule = false;
                            return@let
                        }
                        create()
                    }
                }

                is NetworkResult.Error -> {
                }

                is NetworkResult.Loading -> {

                }
            }

        }
    }

    private suspend fun create() {
        val ref = PreferenceHelper.customPreference(context = this)
        val user = ref.userInfo;
        repository.onCreateTicket(
            request = CreateTicketRequest(
                aType = 30,
                hiCardNo = "",
                webAccountUserID = user.webUserAccID,
                patientCode = user.pkhid,
                patientName = user.accName,
                ticketGetTime = DateTimeUtils.getTomorrow(),
                webAccManPtID = 896,
            )
        ).collect { values ->
            PreferenceHelper.customPreference(context = this).sechule = false;
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
        Log.e("Asaas", "userInfo11")
        coroutineScope.cancel();
    }

    private fun stopService() {
        alarmMgr?.cancel(alarmIntent)
        Log.e("Asaas", "stopService")
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show()
    }

    private val repository: Repository = Repository(RemoteDataSource())

}

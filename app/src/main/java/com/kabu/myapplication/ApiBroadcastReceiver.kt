package com.kabu.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.kabu.myapplication.repository.Repository
import com.kabu.myapplication.viewmodel.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ApiBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch() {
//            var refpose: Repository = Repository();
        }
        val serviceIntent = Intent(context, ApiService::class.java)
        context.stopService(serviceIntent)
    }
}
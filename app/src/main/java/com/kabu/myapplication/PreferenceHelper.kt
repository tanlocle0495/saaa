package com.kabu.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.kabu.myapplication.model.UserInfoResponse


object PreferenceHelper {

    val USER_ID = "USER_ID"
    val USER_PASSWORD = "PASSWORD"

    fun defaultPreference(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun customPreference(context: Context): SharedPreferences =
        context.getSharedPreferences("MYPREFER", Context.MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    var SharedPreferences.token
        get() = getString("token", "")
        set(value) {
            editMe {
                it.putString("token", value)
            }
        }

    var SharedPreferences.userName
        get() = getString("userName", "0707940328")
        set(value) {
            editMe {
                it.putString("userName", value)
            }
        }

    var SharedPreferences.passWord
        get() = getString("passWord", "82059962")
        set(value) {
            editMe {
                it.putString("passWord", value)
            }
        }


    var SharedPreferences.userInfo: UserInfoResponse
        get() {
            val gson = Gson()
            val data = getString("userInfo", "")
            val json = gson.fromJson(data, UserInfoResponse::class.java)
            return json
        }
        set(value: UserInfoResponse) {
            val gson = Gson()
            editMe {
                it.putString("userInfo", gson.toJson(value))
            }
        }

    var SharedPreferences.sechule
        get() = getBoolean("sechule", false)
        set(value) {
            editMe {
                it.putBoolean("sechule", value)
            }
        }

    fun onClear(context: Context) {
        defaultPreference(context).edit().clear().apply();
    }
//    var SharedPreferences.password
}


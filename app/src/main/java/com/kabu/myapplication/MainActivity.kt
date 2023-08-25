package com.kabu.myapplication

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import com.kabu.myapplication.PreferenceHelper.passWord
import com.kabu.myapplication.PreferenceHelper.sechule
import com.kabu.myapplication.PreferenceHelper.token
import com.kabu.myapplication.PreferenceHelper.userInfo
import com.kabu.myapplication.PreferenceHelper.userName
import com.kabu.myapplication.api.RetrofitSingletone
import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.model.UserInfoResponse
import com.kabu.myapplication.repository.NetworkResult
import com.kabu.myapplication.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var progress: View;
    lateinit var btnCreateTicket: Button;
    lateinit var btnTicketList: Button;
    lateinit var ref: SharedPreferences;
    private lateinit var userName: EditText
    lateinit var logedContainer: View
    lateinit var password: EditText

    lateinit var txtName: TextView
    lateinit var txtIdentify: TextView
    lateinit var txtUserAccount: TextView
    lateinit var txtUserId: TextView

    lateinit var txtRoom: TextView
    lateinit var txtTicketNo: TextView
    lateinit var txtTicketDate: TextView
    lateinit var imgQRApoiment: ImageView
    lateinit var btnLogout: Button
    lateinit var btnTurnSchedule: Button

    lateinit var img: ImageView

    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ref = PreferenceHelper.customPreference(context = this)
        RetrofitSingletone.accessToken = ref.token ?: ""
        initView()
        initEvent()

    }

    override fun onResume() {
        super.onResume()
        if (ref.token?.isNotEmpty() == true) {
            userInfo()
        } else {
            val serviceIntent = Intent(this, ApiService::class.java)
            stopService(serviceIntent);
            ref.sechule = false
            findViewById<View>(R.id.containerLogin).visibility = View.VISIBLE
        }

    }

    private fun initEvent() {
        btnTicketList.setOnClickListener {
            startActivity(Detailtctivity.newIntent(this, ref.userInfo.sub))
        }

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            onLogin()
        }

        findViewById<Button>(R.id.btnLoginGeticket).setOnClickListener {
            onLogin(true)
        }

        btnCreateTicket.setOnClickListener {
            onCreate(ref.userInfo);

        }

    }

    private fun initView() {
        userName = findViewById(R.id.edtUseName);
        password = findViewById(R.id.edtPassword);
        logedContainer = findViewById(R.id.logedContainer)
        progress = findViewById(R.id.progress)
        progress.setOnClickListener {};
        btnCreateTicket = findViewById(R.id.buttonCreateTicket)
        btnTicketList = findViewById(R.id.buttonTicketList)
        userName.setText(ref.userName)
        password.setText(ref.passWord)
        ///logged
        txtName = findViewById(R.id.txtNameDisplay)
        btnLogout = findViewById(R.id.btnLogout)
        btnTurnSchedule = findViewById(R.id.btnTurnSchedule)
        imgQRApoiment = findViewById(R.id.imgQRApoiment)
        txtIdentify = findViewById(R.id.txtIdentify)
        txtUserAccount = findViewById(R.id.txtAccount)
        txtUserId = findViewById(R.id.txtUserId)
        img = findViewById(R.id.imgUserId)

        txtRoom = findViewById(R.id.txtRoom)
        txtTicketNo = findViewById(R.id.ticketNumberText)
        txtTicketDate = findViewById(R.id.txtDateApoiment)
        if (ref.sechule) {
            btnTurnSchedule.setText("Tự động tạo DS 12h ngày mai:Đang bật")
            btnTurnSchedule.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.btn_on,
                    null
                )
            )
        } else {
            btnTurnSchedule.setText("Tự động tạo DS 12h ngày mai:Đang tắt")
            btnTurnSchedule.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.btn_off,
                    null
                )
            )
        }
        userName.addTextChangedListener {
            ref.userName = it.toString()
        }
        password.addTextChangedListener {
            ref.passWord = it.toString()
        }
        btnLogout.setOnClickListener {
            ref.edit().clear().apply();
            val intent = Intent(this, MainActivity::class.java)
            val serviceIntent = Intent(this, ApiService::class.java)
            stopService(serviceIntent);
            ref.sechule = false;
            startActivity(intent);
            finish();
        }

        btnTurnSchedule.setOnClickListener {
            if (!ref.sechule) {
                onLoginWhenStartService()
            } else {
                showStopServiceDialog()
            }
        }
    }

    fun disableView() {
        findViewById<View>(R.id.containerLogin).visibility = View.GONE
    }

    fun onShowLoggedInfo(userInfo: UserInfoResponse) {
        logedContainer.visibility = View.VISIBLE
        txtName.text = " Tên Bệnh nhân: ${userInfo.accName}"
        txtIdentify.text = "CCCD: ${userInfo.cccd}"
        txtUserAccount.text = "Tên đăng nhập: ${userInfo.accUserName}"
        txtUserId.text = "Mã bệnh nhân: ${userInfo.pkhid}"
        img.visibility = View.GONE
        userInfo.pkhid?.run {
            if (isNotEmpty()) {
                img.visibility = View.VISIBLE
                img.setImageBitmap(QRCodeGenerator.generateBarcode(this, 250, 70))
            }
        }

    }

    fun displayTicket(ticket: TicketResponse) {
        findViewById<View>(R.id.containerTicket).visibility = View.VISIBLE
        val room = ticket.ticketNumberText.split("-")[0];
        txtRoom.text = "Quày đăng ký số: ${room}"
        txtTicketNo.text = "Số thứ tự: ${ticket.ticketNumberText}"
        txtTicketDate.text = "Ngày khám: ${DateTimeUtils.dayDisplay(ticket.issueDateTime)}"
        ticket.serialTicket?.let {
            imgQRApoiment.setImageBitmap(QRCodeGenerator.generateQRCode("qms$it", 250, 250))
        }
    }

    private fun onCheckTicket(
        user: UserInfoResponse,
        createTicket: Boolean = false,
    ) {
        progress.visibility = View.VISIBLE;
        viewModel.getTicket(user.sub)
        viewModel.ticket.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    progress.visibility = View.GONE
                    response.data?.let {
                        if (it.isNotEmpty()) {
                            displayTicket(ticket = it[0])
                            btnCreateTicket.visibility = View.GONE
                            return@let
                        }
                        btnCreateTicket.visibility = View.VISIBLE
                        findViewById<View>(R.id.containerTicket).visibility = View.GONE
                        if (createTicket) {
                            onCreate(infor = user)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    findViewById<View>(R.id.containerTicket).visibility = View.GONE
                    progress.visibility = View.GONE
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun onCreate(infor: UserInfoResponse) {
        progress.visibility = View.VISIBLE;
        viewModel.createTicket(infor)
        viewModel.createTicket.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    progress.visibility = View.GONE
                    startActivity(Detailtctivity.newIntent(this, ref.userInfo.sub));
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE
                    Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    private fun onLogin(createTicket: Boolean = false) {
        progress.visibility = View.VISIBLE;
        viewModel.onLogin(username = userName.text.toString(), password = password.text.toString())
        viewModel.loginOb.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        ref.token = "${it.token_type} ${it.access_token}"
                        RetrofitSingletone.accessToken = ref.token ?: ""
                        userInfo(createTicket)
                    }
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE
                    btnCreateTicket.visibility = View.VISIBLE
                    Toast.makeText(this, "${response.message} ${response.data}", Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }


    private fun userInfo(createTicket: Boolean = false, startService: Boolean = false) {
        viewModel.fetchUserInfo()
        progress.visibility = View.VISIBLE
        viewModel.userInfoResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    btnLogout.visibility = View.VISIBLE;
                    response.data?.let {
                        disableView()
                        onShowLoggedInfo(it)
                        ref.userInfo = it;
                        progress.visibility = View.GONE
                        onCheckTicket(it, createTicket);
                    }
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE
                    if (response.code == 401) {
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }


    private fun userInfoCheck() {
        viewModel.fetchUserInfo2()
        progress.visibility = View.VISIBLE
        viewModel.userInfoResponse2.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    btnLogout.visibility = View.VISIBLE;
                    progress.visibility = View.GONE
                    response.data?.let {
                        disableView()
                        onShowLoggedInfo(it)
                        ref.userInfo = it;
                        btnTurnSchedule.setText("Tự động tạo DS 12h ngày mai: Đang bật")
                        btnTurnSchedule.setBackgroundColor(
                            ResourcesCompat.getColor(
                                resources,
                                R.color.btn_on,
                                null
                            )
                        )
                        ref.sechule = true;
                        startService();
                    }
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE
                    if (response.code == 401) {
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                    }
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }

    private fun onLoginWhenStartService() {
        progress.visibility = View.VISIBLE;
        viewModel.onLogin2(username = userName.text.toString(), password = password.text.toString())
        viewModel.loginOb2.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        ref.token = "${it.token_type} ${it.access_token}"
                        RetrofitSingletone.accessToken = ref.token ?: ""
                        Log.e("userInfo", "startService");
                        userInfoCheck();
                    }
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE
                    btnCreateTicket.visibility = View.VISIBLE
                    Toast.makeText(this, "${response.message} ${response.data}", Toast.LENGTH_SHORT)
                        .show()
                }

                is NetworkResult.Loading -> {

                }
            }
        }
    }

    //    void showDialog
    fun startService() {
        val serviceIntent = Intent(this, ApiService::class.java)
        startService(serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, ApiService::class.java)
        stopService(serviceIntent)
    }

    private fun showStopServiceDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Thông báo")
        alertDialogBuilder.setMessage("Bạn có muốn dừng dịch vụ?")
        alertDialogBuilder.setPositiveButton("Xác nhận") { dialog: DialogInterface, which: Int ->
            ref.sechule = false;
            btnTurnSchedule.setText("Tự động tạo DS 12h ngày mai: Đang tắt")
            btnTurnSchedule.setBackgroundColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.btn_off,
                    null
                )
            )
            stopService();
        }
        alertDialogBuilder.setNegativeButton("Hủy") { dialog: DialogInterface, which: Int ->
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}


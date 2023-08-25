package com.kabu.myapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kabu.myapplication.adapter.ListDetailAdapter
import com.kabu.myapplication.adapter.OnItemClickListener
import com.kabu.myapplication.model.TicketResponse
import com.kabu.myapplication.repository.NetworkResult
import com.kabu.myapplication.viewmodel.DetailViewModel
import com.kabu.myapplication.viewmodel.MainViewModel

class Detailtctivity : AppCompatActivity() {

    private val viewModel: DetailViewModel by viewModels()

    private lateinit var progress: View
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        progress = findViewById(R.id.progress)
        recyclerView = findViewById(R.id.recycleView)
        recyclerView.setHasFixedSize(true);
        recyclerView.layoutManager = LinearLayoutManager(this);
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
        val id = intent.getStringExtra("id");
        Toast.makeText(this, "$id", Toast.LENGTH_SHORT).show();
        onCheckTicket()
    }

    private fun onCheckTicket() {
        progress.visibility = View.VISIBLE;
        viewModel.getTickets()
        viewModel.ticket.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    progress.visibility = View.GONE;
                    response.data?.let {
                        if (it.isNotEmpty()) {
                            val adapter = ListDetailAdapter(it).setOnItemClickListener(object :
                                OnItemClickListener {
                                override fun onItemClick(item: TicketResponse) {
                                    Toast.makeText(
                                        this@Detailtctivity,
                                        "Clicked item: ${item.ticketID}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                            recyclerView.adapter = ListDetailAdapter(it)

                        }
                    }
                }

                is NetworkResult.Error -> {
                    progress.visibility = View.GONE;
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context, id: String): Intent {
            val intent = Intent(context, Detailtctivity::class.java)
            intent.putExtra("id", id)
            return intent
        }
    }
}
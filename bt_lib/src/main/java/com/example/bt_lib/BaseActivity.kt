package com.example.bt_lib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
//        initRcView()
        supportFragmentManager.beginTransaction().replace(R.id.placeHolder, DeviceListFragment())
            .commit()
    }

//    private fun initRcView() {
//        val rcView = findViewById<RecyclerView>(R.id.rcViewPaired)
//        rcView.layoutManager = LinearLayoutManager(this)
//        val adapter = DeviceAdapter()
//        rcView.adapter = adapter
//        adapter.submitList(createListDevices())
//    }

    private fun createListDevices(): List<DeviceItem> {
        val list = ArrayList<DeviceItem>()
        for (i in 0 until 5) {
            list.add(DeviceItem("Device ${i + 1}", "192.255.127.38", false))
        }
        return list
    }
}
package com.merpyzf.itemdecorationdemo

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dataList = mutableListOf<String>()
        for (i in 0 until 20) {
            dataList.add("hello $i")
        }
        val adapter = RvAdapter(dataList, R.layout.rv_item)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DateLineItemDecoration.Builder()
                .timeLineBgColor(Color.WHITE)
                .textColor(Color.WHITE)
                .textCircleBgColor(Color.GREEN)
                .textColor(Color.RED)
                .getSectionInfo {
                    DateLineItemDecoration.Section().apply {
                        isFirst = true
                        isEnd = true
                        date = if (it < 10) {
                            "0$it"
                        } else {
                            "$it"
                        }
                    }
                }
                .build(this)
        )
        adapter.notifyDataSetChanged()
    }
}
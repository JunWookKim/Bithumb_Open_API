package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityHistoryDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryDetailActivity : AppCompatActivity() {
    private val binding by lazy{ActivityHistoryDetailBinding.inflate(layoutInflater)}
    lateinit var db : AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table").build()

        val name = intent.getStringExtra("name")
        val timestamp = intent.getStringExtra("timestamp")
        Log.d("history", "${"$name $timestamp"}")
        CoroutineScope(Dispatchers.IO).launch{
            Log.d("history", db.priceDao().getByNameAndDate(name!!, timestamp!!.toLong()).toString())
        }
    }
}
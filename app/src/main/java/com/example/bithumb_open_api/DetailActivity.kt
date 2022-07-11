package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityDetailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity(), CoroutineScope {
    private val binding by lazy{ActivityDetailBinding.inflate(layoutInflater)}
    lateinit var db : AppDatabase
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table").build()
        launch {
            val job = async {
                val name = intent.getStringExtra("name")
                name
            }
            val name = job.await()
            val closingPriceList = db.priceDao().getClosingPriceByName(name)
            val timestampList = db.priceDao().getTimestampByName(name)

            withContext(Dispatchers.Main){
                val detailRecyclerAdapter = DetailRecyclerAdapter(name, closingPriceList, timestampList)
                binding.recyclerview.adapter = detailRecyclerAdapter
                binding.recyclerview.layoutManager = LinearLayoutManager(this@DetailActivity)
            }
        }
    }
}
package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityHistoryDetailBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class HistoryDetailActivity : AppCompatActivity(), CoroutineScope{
    private val binding by lazy{ActivityHistoryDetailBinding.inflate(layoutInflater)}
    lateinit var db : AppDatabase
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table").build()

        val name = intent.getStringExtra("name")
        val timestamp = intent.getStringExtra("timestamp")
        Log.d("history", "$name $timestamp")
        launch{
            setUpToolBar(binding.toolBar, name)
            showDetail(db.priceDao().getByNameAndDate(name!!, timestamp!!.toLong()))
        }
    }

    private suspend fun showDetail(result: IntegratedInfo) = withContext(Dispatchers.Main){
        with(binding){
            textName.text = result.name
            textOpening.text = result.opening
            textClosing.text = result.closing
            textMin.text = result.min
            textMax.text = result.max
            textUnitTraded.text = result.unitTraded
            textTradeValue.text = result.tradedValue
            textUnitTraded24H.text = result.unitTraded24H
            textTradeValue24H.text = result.tradeValue24H
            textFluctate24H.text = result.fluctate24H
            textFluctateRate24H.text = result.fluctateRate24H
            textPrevClosing.text = result.prevClosing
            textTimestamp.text = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA).format(result.date)
        }
    }

    private suspend fun setUpToolBar(toolBar: Toolbar, name: String?) = withContext(Dispatchers.Main){
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolBar.title = "$name 상세"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
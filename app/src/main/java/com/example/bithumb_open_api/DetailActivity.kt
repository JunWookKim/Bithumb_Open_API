package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityDetailBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
            val name = intent.getStringExtra("name")
            val closingPriceList = db.priceDao().getClosingPriceByName(name)
            val timestampList = db.priceDao().getTimestampByName(name)

            setUpChart(name, closingPriceList, timestampList)
            setUpRecyclerView(name, closingPriceList.asReversed(), timestampList.asReversed())
            setUpToolBar(binding.toolBar, name)
        }

    }

    private fun setUpToolBar(toolBar: Toolbar, name: String?) {
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolBar.title = name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private suspend fun setUpRecyclerView(name: String?, closingPriceList: List<String>, timestampList: List<Long>) = withContext(Dispatchers.Main){
        val detailRecyclerAdapter = DetailRecyclerAdapter(name, closingPriceList, timestampList)
        Log.d("RV_get_time", timestampList.toString())
        Log.d("RV_size", timestampList.size.toString())
        binding.recyclerview.adapter = detailRecyclerAdapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this@DetailActivity)
        detailRecyclerAdapter.setItemClickListener(object: DetailRecyclerAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d("detail_position", position.toString())
            }
        })
    }

    //Line Chart 생성 함수
    private suspend fun setUpChart(name: String?, closingPriceList: List<String>, timestampList: List<Long>) = withContext(Dispatchers.IO){

        with(binding.lineChart){
            //x 축 설정
            xAxis.position = XAxis.XAxisPosition.BOTTOM

            //y 축 설정
            val yAxis = axisLeft
            yAxis.axisMinimum = closingPriceList.min().toFloat()
            yAxis.axisMaximum = closingPriceList.max().toFloat()
        }

        //Data Entry 생성
        setData(name, closingPriceList, timestampList)
    }

    private suspend fun setData(name: String?, closingPriceList: List<String>, timestampList: List<Long>) = withContext(Dispatchers.IO){
        Log.d("Chart_get_price", closingPriceList.toString())
        Log.d("Chart_get_time", timestampList.toString())
        val values = ArrayList<Entry>()
        for (i in closingPriceList.indices){
//            values.add(Entry(timestampList[i].toFloat(), closingPriceList[i].toFloat()))
            values.add(Entry(i.toFloat(), closingPriceList[i].toFloat()))
        }

        val set1 : LineDataSet

        if (binding.lineChart.data != null && binding.lineChart.data.dataSetCount > 0){
            set1 = binding.lineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.lineChart.data.notifyDataChanged()
            binding.lineChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, name)
            set1.setDrawIcons(false)
            set1.enableDashedLine(10f, 5f, 0f)
            set1.axisDependency = YAxis.AxisDependency.LEFT

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            val data = LineData(dataSets)
            binding.lineChart.data = data
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
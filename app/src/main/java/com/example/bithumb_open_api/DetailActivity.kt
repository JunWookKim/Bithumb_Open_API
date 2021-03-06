package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity(), CoroutineScope {
    private val binding by lazy{ActivityDetailBinding.inflate(layoutInflater)}
    lateinit var db : AppDatabase
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    var lastClickedTime = 0L
    var nowTimestamp = 0L

    var keyList = mutableListOf<String>()
    var infoList = mutableListOf<Map<String, String>>()
    var integratedInfoList = mutableListOf<IntegratedInfo>()

    private val retrofitService = IRetrofitService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table").build()
        val name = intent.getStringExtra("name")
        launch {
            val closingPriceList = db.priceDao().getClosingPriceByName(name)
            val timestampList = db.priceDao().getTimestampByName(name)

            setUpChart(name, closingPriceList, timestampList)
            setUpRecyclerView(name, closingPriceList.asReversed(), timestampList.asReversed())
        }
        setUpToolBar(binding.toolBar, name)

        binding.fab.setOnClickListener {
            it.animate().rotationBy(360f)
            //????????? ?????????????????? ?????????
            if (SystemClock.elapsedRealtime() - lastClickedTime > 1000){
                launch {
                    getAndSetData()
                    refreshDB()
                    val closingPriceList = db.priceDao().getClosingPriceByName(name)
                    val timestampList = db.priceDao().getTimestampByName(name)

                    setUpChart(name, closingPriceList, timestampList)
                    setUpRecyclerView(name, closingPriceList.asReversed(), timestampList.asReversed())
                }
            }
            else{
                Toast.makeText(this, "Too fast!!", Toast.LENGTH_SHORT).show()
            }
            lastClickedTime = SystemClock.elapsedRealtime()
        }
    }

    private fun refreshDB(){
        for (x in integratedInfoList){
            db.priceDao().insertInfo(x)
        }
    }

    private suspend fun getAndSetData() = withContext(Dispatchers.IO){
        //???????????? ???????????? list ?????????
        keyList.clear()
        infoList.clear()
        integratedInfoList.clear()

        var json : JSONObject
        launch{
            json = JSONObject(retrofitService.getData().data.toString())
            parseJson(json)
            //integratedInfoList ?????????
            for(i in 0 until infoList.size){
                val model = IntegratedInfo(keyList[i], infoList[i]["opening_price"], infoList[i]["closing_price"], infoList[i]["min_price"], infoList[i]["max_price"],
                    infoList[i]["units_traded"], infoList[i]["acc_trade_value"], infoList[i]["units_traded_24H"], infoList[i]["acc_trade_value_24H"], infoList[i]["fluctate_24H"],
                    infoList[i]["fluctate_rate_24H"], infoList[i]["prev_closing_price"], nowTimestamp)
                integratedInfoList.add(model)
            }
            Log.d("size", integratedInfoList.size.toString())
        }
    }

    private suspend fun parseJson(json: JSONObject) = withContext(Dispatchers.Default){
        val keys = json.keys()
        while(keys.hasNext()){
            keyList.add(keys.next())
        }

        for (key in keyList){
            if (key == "date"){
                nowTimestamp = json.getString(key).toLong()
                Log.d("nowTimestamp", nowTimestamp.toString())
            } else{
                val result = json.getJSONObject(key)
//                Log.d("Retrofit_result", "$key : $result")
                val map = stringToMap(result.toString())
//                Log.d("Retrofit_map", "$key : $map")
                infoList.add(map)
            }
        }
    }

    private fun setUpToolBar(toolBar: Toolbar, name: String?){
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

    //Line Chart ?????? ??????
    private suspend fun setUpChart(name: String?, closingPriceList: List<String>, timestampList: List<Long>){
        val dateTimestampList = mutableListOf<String?>()
        for(x in timestampList){
            dateTimestampList.add(SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA).format(x).toString())
        }

        with(binding.lineChart){
            description.isEnabled = false   // ?????? ?????? ?????? ??????
            legend.isEnabled = false        // ?????? ?????? ??????
            marker = CustomMarkerView(context, R.layout.markerview) // marker ??????

            //x ??? ??????
            with(xAxis){
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = ChartCustomFormatter(dateTimestampList)    // valueFormatter ??????
                granularity = 1f    // ?????? ??????
            }

            //y ??? ??????
            with(axisLeft){
                axisMinimum = closingPriceList.min().toFloat()  // ????????? ??????
                axisMaximum = closingPriceList.max().toFloat() + ((closingPriceList.max().toFloat() - closingPriceList.min().toFloat()) / 4)    // ????????? ??????
                setDrawLabels(true) // ?????? ?????? ??????
            }
            axisRight.isEnabled = false //y ??? ????????? ?????? ??????
        }

        //Data Entry ??????
        setData(name, closingPriceList, timestampList)
    }

    private suspend fun setData(name: String?, closingPriceList: List<String>, timestampList: List<Long>){
        Log.d("Chart_get_price", closingPriceList.toString())
        Log.d("Chart_get_time", timestampList.toString())
        val values = ArrayList<Entry>()
        for (i in closingPriceList.indices){
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
        //        set1.setDrawValues(false) // value ??? ?????? ??????
        binding.lineChart.setVisibleXRangeMaximum(3f)
        set1.valueTextSize = 15f
        set1.lineWidth = 2f
        binding.lineChart.invalidate()
    }

    private fun stringToMap(string: String): Map<String, String> {
        val processedString = string.replace("{", "").replace("}", "").replace("\"", "")
        val map = processedString.split(",").associate{
            val (left, right) = it.split(":")
            left to right
        }
        return map
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
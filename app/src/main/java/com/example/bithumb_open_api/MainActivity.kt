package com.example.bithumb_open_api

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.rangeTo
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    var keyList = mutableListOf<String>()
    var infoList = mutableListOf(mapOf<String, String>())
    var integratedInfoList = mutableListOf<IntegratedInfo>()
    var clickedPosition = 0
    var date = 0L
    private val retrofitService = IRetrofitService.create()

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table")
            .build()

        val retrofitRecyclerAdapter = RecyclerAdapter(keyList, infoList)
        retrofitRecyclerAdapter.setItemClickListener(object: RecyclerAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int){
                clickedPosition = position
                Log.d("position : ", clickedPosition.toString())
            }
        })

        CoroutineScope(Dispatchers.IO).launch {
            db.priceDao().deleteAll()
            getDataFromRetrofit()
            for(i in 0 until integratedInfoList.size){
                db.priceDao().insertInfo(integratedInfoList[i])
            }
            Log.d("Coroutine", db.priceDao().getAll().toString())
            Log.d("Coroutine", coroutineContext.toString())

            withContext(Dispatchers.Main){
                setUpRecyclerView(retrofitRecyclerAdapter)
            }
        }

        binding.btnRefresh.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                getDataFromRetrofit()

//                val random = keyList.random()
//                Log.d("DB", random + " " + db.priceDao().get(random).toString())

                withContext(Dispatchers.Main){
                    setUpRecyclerView(retrofitRecyclerAdapter)
                }
            }
        }
    }

    private suspend fun getDataFromRetrofit() {
        keyList.clear()
        infoList.clear()
        val json = JSONObject(retrofitService.getData().data.toString())
        val keys = json.keys()
        while(keys.hasNext()){
            keyList.add(keys.next().toString())
        }
        Log.d("Retrofit_key", keyList.toString())
        for (key in keyList){
            if (key == "date"){
                date = json.getString(key).toLong()
            }
            else{
                val result = json.getJSONObject(key)
                Log.d("Retrofit_result", "$key : $result")
                val map = stringToMap(result.toString())
                Log.d("Retrofit_map", "$key : $map")
                infoList.add(map)
            }
        }

        for(i in 0 until infoList.size){
            val model = IntegratedInfo(keyList[i], infoList[i]["opening_price"], infoList[i]["closing_price"], infoList[i]["min_price"], infoList[i]["max_price"],
                infoList[i]["units_traded"], infoList[i]["acc_trade_value"], infoList[i]["units_traded_24H"], infoList[i]["acc_traded_24H"], infoList[i]["fluctate_24H"],
                infoList[i]["fluctate_rate_24H"], infoList[i]["prev_closing_price"], date)
            integratedInfoList.add(model)
        }
    }

    private fun setUpRecyclerView(recyclerAdapter: RecyclerAdapter) {
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scrollToPosition(clickedPosition)
    }

    private fun stringToMap(string: String): Map<String, String> {
        val processedString = string.replace("{", "").replace("}", "").replace("\"", "")
        val map = processedString.split(",").associate{
            val (left, right) = it.split(":")
            left to right
        }
        return map
    }
}
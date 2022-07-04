package com.example.bithumb_open_api

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    var keyList = mutableListOf<String>()
    var infoList = mutableListOf(mapOf<String, String>())
    var clickedPosition = 0
    val retrofitService = IRetrofitService.create()

    lateinit var db : AppDatabase

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
        getDataFromRetrofit(retrofitRecyclerAdapter)

        binding.btnRefresh.setOnClickListener {
            getDataFromRetrofit(retrofitRecyclerAdapter)
        }
    }

    private fun getDataFromRetrofit(recyclerAdapter: RecyclerAdapter) {
        retrofitService.getData().enqueue(object : retrofit2.Callback<ResponseModel>{
            override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                keyList.clear()
                infoList.clear()
                Log.d("Retrofit_success", response.body()?.data.toString())
                val json = JSONObject(response.body()?.data.toString())
                val keys = json.keys()
                while(keys.hasNext()){
                    keyList.add(keys.next().toString())
                }
                Log.d("Retrofit_key", keyList.toString())
                for (key in keyList){
                    if (key != "date"){
                        val result = json.getJSONObject(key)
                        Log.d("Retrofit_result", "$key : $result")
                        val map = stringToMap(result.toString())
                        Log.d("Retrofit_map", "$key : $map")
                        infoList.add(map)
                    }
                }

//                CoroutineScope(Dispatchers.IO).launch {
//                    db.priceDao().insertInfo(Info(keyList[0], infoList[0]["opening_price"]!!, infoList[0]["closing_price"]!!, infoList[0]["min_price"]!!, infoList[0]["max_price"]!!,
//                        infoList[0]["units_traded"]!!, infoList[0]["acc_trade_value"]!!, infoList[0]["units_traded_24H"]!!, infoList[0]["acc_traded_24H"]!!, infoList[0]["fluctate_24H"]!!,
//                        infoList[0]["fluctate_rate_24H"]!!, infoList[0]["prev_closing_price"]!!, infoList[0]["date"]!!))
//                }

                recyclerAdapter.notifyDataSetChanged()
                setUpRecyclerView(recyclerAdapter)
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.d("Retrofit_failed", t.message.toString())
            }
        })
    }

    private fun setUpRecyclerView(recyclerAdapter: RecyclerAdapter) {
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scrollToPosition(clickedPosition)
    }

    private fun updateDB(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("DB", db.priceDao().getAll().toString())
            //TODO DB에 있는 시각과 현재 시각비교후 DB Update문 작성
        }
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
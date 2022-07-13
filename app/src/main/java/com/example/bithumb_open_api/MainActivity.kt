package com.example.bithumb_open_api

import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.bithumb_open_api.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}

    var keyList = mutableListOf<String>()
    var infoList = mutableListOf(mapOf<String, String>())
    var integratedInfoList = mutableListOf<IntegratedInfo>()
    var clickedPosition = 0

    var nowTimestamp = 0L
    private val retrofitService = IRetrofitService.create()
    private var lastClickedTime = 0L

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private lateinit var db : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //RoomDB 초기화
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "info_table")
            .build()

        //RecyclerAdapter 초기화
        val recyclerAdapter = RecyclerAdapter(keyList, infoList)

        //초기 실행 화면
        launch {
            getAndSetData()
            refreshDB()
            setUpRecyclerView(recyclerAdapter)
            setUpAppBar(binding.toolBar)
        }

        //fab onClickListener
        binding.fab.setOnClickListener {
            it.animate().rotationBy(360f)
            //빠르게 클릭하는것을 방지함
            if (SystemClock.elapsedRealtime() - lastClickedTime > 1000){
                launch {
                    getAndSetData()
                    refreshDB()
                    searchInDB(binding.editTextSearch.text.toString())
                    setUpAppBar(binding.toolBar)
                }
            }
            else{
                Toast.makeText(this, "Too fast!!", Toast.LENGTH_SHORT).show()
            }
            lastClickedTime = SystemClock.elapsedRealtime()
        }

        //searchBar 구현
        binding.editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchInDB(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.btnClearDB.setOnClickListener{
            launch {
                db.priceDao().deleteByDate(db.priceDao().getMinDate())
            }
        }
        binding.btnShowDB.setOnClickListener{
            launch{
                Log.d("DB_BTC", db.priceDao().getBTC().toString())
                Log.d("DB_get_all", db.priceDao().getAll().toString())
            }
        }
    }

    private suspend fun setUpAppBar(toolBar: Toolbar) = withContext(Dispatchers.Main){
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolBar.title = convertTimestampToDate(nowTimestamp)
    }

    //DB 에서 입력값 확인 후 해당 값만 출력
    private fun searchInDB(search: String) {
        launch {
            val job = async(Dispatchers.IO){
                val result = db.priceDao().getLatestValueByName(search, db.priceDao().getMaxDate())
                val searchedKey = mutableListOf<String>()
                val searchedValue = mutableListOf<Map<String, String>>()

                for (x in result){
                    searchedKey.add(x.name)
                    searchedValue.add(mapOf("opening_price" to x.opening.toString(), "closing_price" to x.closing.toString(), "min_price" to x.min.toString(), "max_price" to x.max.toString(), "units_traded" to x.unitTraded.toString(),
                        "acc_trade_value" to x.tradedValue.toString(), "prev_closing_price" to x.prevClosing.toString(), "units_traded_24H" to x.unitTraded24H.toString(),
                        "acc_trade_value_24H" to x.tradeValue24H.toString(), "fluctate_24H" to x.fluctate24H.toString(), "fluctate_rate_24H" to x.fluctateRate24H.toString()))
                }
                Log.d("Search", searchedKey.toString())
                Log.d("Search", searchedValue.toString())
                Pair(searchedKey, searchedValue)
            }

                val (searchedKey, searchValue) = job.await()
                val searchedAdapter = RecyclerAdapter(searchedKey, searchValue)
                setUpRecyclerView(searchedAdapter)
        }
    }

    //DB 초기화 후 새로운 값으로 채우기
    private suspend fun refreshDB() = withContext(Dispatchers.IO){
////        db.priceDao().deleteAll()
//        //DB가 비어있을 경우 그냥 삽입
//        if (db.priceDao().getAll().isEmpty()){
//            Log.d("DB_empty_insert_first", db.priceDao().getAll().toString())
//
//        }
//
//        val maxDateInDB = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss").format(db.priceDao().getMaxDate())
//        Log.d("DB_max_date_before", maxDateInDB.toString())
//        Log.d("DB_max_date", maxDateInDB.toString().substring(0, 19))
//        Log.d("now_date", convertTimestampToDate(nowTimestamp)!!.substring(0, 19))
//
//        //10분 단위가 지나지 않았다면 가장 최근 데이터를 삭제
//        if (maxDateInDB.toString().substring(0, 19) == convertTimestampToDate(nowTimestamp)!!.substring(0, 19)){
//            Log.d("DB_delete", maxDateInDB)
//            db.priceDao().deleteMaxDate((db.priceDao().getMaxDate()))
//        }
//        //새로운 데이터 삽입
//        for (x in integratedInfoList){
//            db.priceDao().insertInfo(x)
//        }
////        Log.d("DB", db.priceDao().getAll().toString())
//        Log.d("DB_max_date_after", SimpleDateFormat("yyyy-MM-dd, hh:mm:ss").format(db.priceDao().getMaxDate()))
//        Log.d("DB_BTC", db.priceDao().getBTC().toString())
//        Log.d("-------------","--------------")
        for (x in integratedInfoList){
            db.priceDao().insertInfo(x)
        }
    }

    //Json 을 통해 keyList, infoList 채우기
    private suspend fun parseJson(json : JSONObject) = withContext(Dispatchers.Default){
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

    //Timestamp -> date 함수
    private fun convertTimestampToDate(timestamp: Long): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd, hh:mm:ss", Locale.KOREA)
        val date = sdf.format(timestamp)
        Log.d("sdf", date.toString())
        return date
    }

    private suspend fun getAndSetData() = withContext(Dispatchers.IO){
        //기존값이 들어있는 list 초기화
        keyList.clear()
        infoList.clear()
        integratedInfoList.clear()
//        Log.d("Coroutine_list_clear", "${keyList.toString() + infoList.toString() + integratedInfoList.toString()}")

        var json : JSONObject
        launch{
            json = JSONObject(retrofitService.getData().data.toString())
            parseJson(json)
            //integratedInfoList 채우기
            for(i in 0 until infoList.size){
                val model = IntegratedInfo(keyList[i], infoList[i]["opening_price"], infoList[i]["closing_price"], infoList[i]["min_price"], infoList[i]["max_price"],
                    infoList[i]["units_traded"], infoList[i]["acc_trade_value"], infoList[i]["units_traded_24H"], infoList[i]["acc_trade_value_24H"], infoList[i]["fluctate_24H"],
                    infoList[i]["fluctate_rate_24H"], infoList[i]["prev_closing_price"], nowTimestamp)
                integratedInfoList.add(model)
            }
            Log.d("size", integratedInfoList.size.toString())
        }
    }

    private suspend fun setUpRecyclerView(recyclerAdapter: RecyclerAdapter) = withContext(Dispatchers.Main){
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recyclerView.scrollToPosition(clickedPosition)
        recyclerAdapter.setItemClickListener(object : RecyclerAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                clickedPosition = position
                Log.d("position : ", clickedPosition.toString())
            }
        })
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
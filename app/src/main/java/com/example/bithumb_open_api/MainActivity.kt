package com.example.bithumb_open_api

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bithumb_open_api.databinding.ActivityMainBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val coinList = mutableListOf("BTC", "ETH", "ETC", "XRP", "BCH", "QTUM", "BTG", "EOS", "ICX",
    "TRX", "ELF", "OMG", "KNC", "GLM", "ZIL", "WAXP", "POWR", "LRC", "STEEM", "STRAX", "ZRX",
    "REP", "SNT", "ADA", "CTXC", "BAT", "THETA", "LOOM", "WAVES", "LINK", "ENJ", "VET", "MTL",
    "IOST", "TMTG", "QKC", "ATOLO", "AMO", "BSV", "ORBS", "TFUEL", "VALOR", "CON", "ANKR", "MIX",
    "CRO", "FX", "CHR", "MBL", "MXC", "FCT2", "TRV", "DAD", "WOM", "SOC", "BOA", "MEV", "SXP",
    "COS", "EL", "BASIC", "HIVE", "XPR", "VRA", "FIT", "EGG", "BORA", "ARPA", "CTC", "APM", "CKB",
    "AERGO", "ANW", "CENNZ", "EVZ", "CYCLUB", "SRM", "QTCON", "UNI", "YFI", "UMA", "AAVE", "COMP",
    "REN", "BAL", "RSR", "NMR", "RLC", "UOS", "SAND", "GOM2", "BEL", "OBSR", "ORC", "POLA", "AWO",
    "ADP", "DVI", "GHX", "MIR", "MVC", "BLY", "WOZX", "ANV", "GRT", "MM", "BIOT", "XNO", "SNX",
    "SOFI", "COLA", "OXT", "LINA", "MAP", "AQT", "PLA", "WIKEN", "CTSI", "MANA", "LPT", "MKR",
    "SUSHI", "ASM", "PUNDIX", "CELR", "ARW", "FRONT", "RLY", "OCEAN", "BFC", "ALICE", "OGN", "COTI",
    "CAKE", "BNT", "XVS", "SWAP", "CHZ", "AXS", "DAO", "SIX", "DAI", "MATIC", "WOO", "ACH", "VELO",
    "XLM", "VSYS", "IPX", "WICC", "ONT", "META", "KLAY", "ONG", "ALGO", "JST", "XTZ", "MLK", "WEMIX",
    "DOT", "ATOM", "SSX", "TEMCO", "HIBS", "DOGE", "KSM", "CTK", "XYM", "BNB", "NFT", "SUN", "XEC",
    "PCI", "SOL", "EGLD", "GO", "DFA", "C98", "MED", "1INCH", "BOBA", "GALA", "BTT", "EFI", "JASMY",
    "TITAN", "REQ", "CSPR", "AVAX", "TDROP", "SPRT", "NPT", "REI", "T", "MBX", "GMT")
    var keyList = mutableListOf<String>()
    var infoList = mutableListOf(mapOf<String, String>())
    var clickedPosition = 0
    val retrofitService = IRetrofitService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val recyclerAdapter = RecyclerAdapter(coinList, infoList)
//        recyclerAdapter.setItemClickListener(object: RecyclerAdapter.OnItemClickListener{
//            override fun onClick(v: View, positon: Int) {
//                clickedPosition = positon
//                Log.d("position : ", clickedPosition.toString())
//            }
//        })
//        getDataFromVolley(coinList, recyclerAdapter)
//        binding.btnRefresh.setOnClickListener {
//            getDataFromVolley(coinList, recyclerAdapter)
//        }

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
//                    Log.d("Retrofit_keys", keys.next().toString())
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
                recyclerAdapter.notifyDataSetChanged()
                setUpRecyclerView(recyclerAdapter)
            }

            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                Log.d("Retrofit_failed", t.message.toString())
            }
        })
    }


    private fun getDataFromVolley(coinList: MutableList<String>, recyclerAdapter: RecyclerAdapter) {
        val url = "https://api.bithumb.com/public/ticker/ALL_KRW"

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            com.android.volley.Response.Listener<JSONObject> { response ->
                infoList.clear()
                for (x in coinList){
                    val data = response.getJSONObject("data").get(x)
                    val map = stringToMap(data.toString())
                    addMapToInfoList(map)
                    Log.d("Volley_result", "$x : $data + ${data.javaClass.name}")
                    Log.d("Volly_map", map.toString())
                }
                recyclerAdapter.notifyDataSetChanged()
                setUpRecyclerView(recyclerAdapter)
            },
            com.android.volley.Response.ErrorListener { error ->
                Log.d("Volley_error", error.toString()) })
        val queue = Volley.newRequestQueue(this)
        queue.add(jsonRequest)
    }

    private fun setUpRecyclerView(recyclerAdapter: RecyclerAdapter) {
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.scrollToPosition(clickedPosition)
    }

    private fun addMapToInfoList(map: Map<String, String>) {
        infoList.add(map)
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
package com.example.bithumb_open_api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bithumb_open_api.databinding.CardviewBinding

class RecyclerAdapter(private val keyList : MutableList<String>, private var infoList : MutableList<Map<String, String>>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    inner class MyViewHolder(val binding : CardviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = MyViewHolder(
        CardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding =(holder as RecyclerAdapter.MyViewHolder).binding
        with(binding){
            textName.text = keyList[position]
            textOpening.text = infoList[position]["opening_price"]
            textClosing.text = infoList[position]["closing_price"]
            textMin.text = infoList[position]["min_price"]
            textMax.text = infoList[position]["max_price"]
            textUnitTraded.text = infoList[position]["units_traded"]
            textTradeValue.text = infoList[position]["acc_trade_value"]
            textPrevClosing.text = infoList[position]["prev_closing_price"]
            textUnitTraded24H.text = infoList[position]["units_traded_24H"]
            textTradeValue24H.text = infoList[position]["acc_trade_value_24H"]
            textFluctate24H.text = infoList[position]["fluctate_24H"]
            textFluctateRate24H.text = infoList[position]["fluctate_rate_24H"]
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, holder.layoutPosition)
//            Log.d("holder position : ", holder.layoutPosition.toString())
        }
    }

    interface OnItemClickListener{
        fun onClick(v: View, positon: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int {
        return keyList.size
    }

}
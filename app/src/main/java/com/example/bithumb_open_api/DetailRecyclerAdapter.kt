package com.example.bithumb_open_api

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bithumb_open_api.databinding.HistoryListBinding
import com.example.bithumb_open_api.databinding.SimpleListBinding

class DetailRecyclerAdapter(
    name: String?,
    private val closingPrice: List<String>,
    private val timestamp: List<Long>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class MyViewHolder(val binding : HistoryListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder  = MyViewHolder(HistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as DetailRecyclerAdapter.MyViewHolder).binding

        with(binding){
            textTimestamp.text = timestamp[position].toString()
            textClosing.text = closingPrice[position]
        }
    }

    override fun getItemCount() = closingPrice.size

}
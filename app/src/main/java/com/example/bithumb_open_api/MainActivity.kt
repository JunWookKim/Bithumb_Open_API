package com.example.bithumb_open_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bithumb_open_api.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}
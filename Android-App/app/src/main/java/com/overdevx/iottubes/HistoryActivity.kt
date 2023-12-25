package com.overdevx.iottubes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.overdevx.iottubes.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.webViewClient= WebViewClient()
        binding.webView.loadUrl("https://docs.google.com/spreadsheets/d/1UK8CJpZI9xBuwvemT8g3Da8MrP4KbzUsFyA220THvoU/edit?usp=drivesdk")
        binding.webView.settings.javaScriptEnabled=true
        binding.webView.settings.setSupportZoom(true)


    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack())
            binding.webView.goBack()
        // if your webview cannot go back
        // it will exit the application
        else
            super.onBackPressed()
    }
}
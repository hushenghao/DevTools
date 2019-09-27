package com.dede.devtools_demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openAct(view: View) {
        val pattern =
            Pattern.compile("^(\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}:\\d{2}.\\d{3})\\s+(\\d+)\\s+(\\d+)\\s+([VDIWEAvdiwea])\\s+(.*?):(\\s+.*?)\$")
        val matcher =
            pattern.matcher("09-27 15:21:41.492  1056  1169 I LIGHT   : [LightSensor.cpp: processEvent: 331] light value is 407")
        if (matcher.find()) {
            for (i in (0..matcher.groupCount()-1)) {
                Log.i("MainActivity", "openAct: " + matcher.group(i))
            }
        }
//        startActivity(Intent(this, SecondActivity::class.java))
    }
}

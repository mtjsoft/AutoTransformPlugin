package cn.mtjsoft.www.autotransformplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Main22Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = TestFragment()
    }
}
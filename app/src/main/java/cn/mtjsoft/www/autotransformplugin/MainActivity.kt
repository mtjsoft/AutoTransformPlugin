package cn.mtjsoft.www.autotransformplugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val testName = TestName()
        testName.age = 18
        KTDataBean("111")
        KTDataBean2("222", 22)
        findViewById<TextView>(R.id.tv_text).setOnClickListener {
            startActivity(Intent(this, Main22Activity::class.java))
        }
    }
}
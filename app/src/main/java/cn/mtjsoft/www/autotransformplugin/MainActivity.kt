package cn.mtjsoft.www.autotransformplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val testName = TestName()
        testName.age = 18
        KTDataBean("111")
        KTDataBean2("222", 22)
    }
}
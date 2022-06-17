package cn.mtjsoft.www.autotransformplugin;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class APP extends Application {

    private String test1 = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

package com.jasonkung.launcher3.app;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;


public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
    }
}

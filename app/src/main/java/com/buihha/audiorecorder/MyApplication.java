package com.buihha.audiorecorder;

import android.app.Application;

import com.buihha.audiorecorder.data.manager.AppInitManager;

/**
 * time:2016/6/17
 * description:
 *
 * @author sunjianfei
 */
public class MyApplication extends Application {
    public static MyApplication gContext;

    @Override
    public void onCreate() {
        super.onCreate();
        gContext = this;
        AppInitManager.getInstance().initializeApp(this);
    }
}

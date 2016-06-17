package com.buihha.audiorecorder.data.manager;

import android.content.Context;
import android.os.Build;

import com.buihha.audiorecorder.BuildConfig;
import com.buihha.audiorecorder.MyApplication;
import com.buihha.audiorecorder.bean.BaseParameterGenerator;
import com.buihha.audiorecorder.bean.SdkEntity;
import com.buihha.audiorecorder.pool.RequestPool;
import com.buihha.audiorecorder.utils.Logger;
import com.zjb.rxokhttp.RxOkHttp;

/**
 * Created by sunjianfei on 16-2-26.
 * 初始化基本参数
 */
public class AppInitManager {
    private static AppInitManager sInstance;
    private static SdkEntity mSdkEntity = new SdkEntity();

    //是否初始化
    private boolean mIsInitialized;

    public static AppInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppInitManager();
        }
        return sInstance;
    }

    public static SdkEntity getSdkEntity() {
        return mSdkEntity;
    }

    public static void destroy() {
        if (null != sInstance) {
            sInstance = null;
        }
    }

    public void initializeApp(Context context) {
        if (mIsInitialized) return;
        //1.标志位
        mIsInitialized = true;
        //1.初始化 Logger（Logger的初始化要放在ZJB-volley的前面)
        Logger.initLog(BuildConfig.DEBUG, null);
        //2.初始化OkHttp
        RxOkHttp.init(MyApplication.gContext, BuildConfig.DEBUG, new BaseParameterGenerator(), null);
        //3.http request 连接池初始化
        RequestPool.gRequestPool.init();
        //4.初始化SDK相关
        initialize();
    }

    private void initialize() {
        //1.处理eid
        mSdkEntity.setDevice("eid");
        mSdkEntity.setAppVersion(BuildConfig.VERSION_CODE + "");
        mSdkEntity.setPlatform("android");
        mSdkEntity.setAppName("okHttp");
        mSdkEntity.setSystemVersion(Build.VERSION.RELEASE);
        mSdkEntity.setBuild(Build.MODEL);
    }

}

package com.zjb.rxokhttp;

import android.content.Context;

import com.zjb.rxokhttp.core.IParametersGenerator;
import com.zjb.rxokhttp.core.cookie.CookieJarImpl;
import com.zjb.rxokhttp.core.cookie.store.MemoryCookieStore;
import com.zjb.rxokhttp.log.HttpLogger;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;


/**
 * time: 2016/5/31
 * description:同步网络请求框架RxOkHttp的入口
 *
 * @author sunjianfei
 */
public class RxOkHttp {
    private static final int READ_TIMEOUT = 10000;
    private static final int WRITE_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;

    public static Context gContext;
    public static boolean sDebug;
    public static IParametersGenerator sParametersGenerator;
    public static String sValidateHost;
    public static OkHttpClient sOkHttpClient;


    /**
     * 同步Volley的初始化
     *
     * @param context      必须是Application的子类
     * @param debug        是否是调试模式
     * @param generator    请求参数的生成器(可能需要添加基本字段和签名字段)
     * @param validateHost https请求模式下需要与服务器进行双向认证的主机名
     */
    public static void init(Context context, boolean debug, IParametersGenerator generator
            , String validateHost) {
        RxOkHttp.gContext = context;
        RxOkHttp.sDebug = debug;
        RxOkHttp.sParametersGenerator = generator;
        RxOkHttp.sValidateHost = validateHost;
        if (null == sOkHttpClient) {
            RxOkHttp.sOkHttpClient =
                    new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .cookieJar(new CookieJarImpl(new MemoryCookieStore()))//cookie
                            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .addInterceptor(new HttpLogger())//添加log拦截器
                            .hostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String hostname, SSLSession session) {
                                    return false;
                                }
                            })
                            .build();
        }
    }

    public static void release() {
        RxOkHttp.gContext = null;
        RxOkHttp.sParametersGenerator = null;
        RxOkHttp.sValidateHost = null;
        RxOkHttp.sOkHttpClient = null;
    }
}

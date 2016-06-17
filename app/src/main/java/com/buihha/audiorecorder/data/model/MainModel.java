package com.buihha.audiorecorder.data.model;

import com.zjb.rxokhttp.core.request.HttpGsonRequest;
import com.zjb.rxokhttp.core.request.RequestBuilder;
import com.zjb.rxokhttp.core.request.RxOkHttpRequest;
import com.zjb.rxokhttp.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.buihha.audiorecorder.pool.RequestPool.gRequestPool;

/**
 * time:2016/6/17
 * description:
 *
 * @author sunjianfei
 */
public class MainModel {


    public Observable<String> request() {
        //1.创建Request
        HttpGsonRequest<String> mRefreshRequest = RequestBuilder.create(String.class)
                //默认post
                .requestMethod(RxOkHttpRequest.Method.GET)
                .url("http://baike.baidu.com/api/openapi/BaikeLemmaCardApi")
                .put("scope", 103)
                .put("format", "json")
                .put("appid", "379020")
                .put("bk_key", "关键字")
                .put("bk_length", 600)
//                //自定义json解析器
//                .parser(new CustomParser())
//                //添加header
//                .putHeaders("Authorization", AppInitManager.getSdkEntity().getToken())
                .build();
        //2.进行数据的处理
        return gRequestPool.request(mRefreshRequest)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}

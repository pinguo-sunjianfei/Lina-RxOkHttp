package com.buihha.audiorecorder.pool;

import com.zjb.rxokhttp.RxOkHttp;
import com.zjb.rxokhttp.core.exception.RxOkHttpError;
import com.zjb.rxokhttp.core.request.RxOkHttpRequest;
import com.zjb.rxokhttp.core.response.HttpResponse;

import java.util.Hashtable;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * time: 2016/6/17
 * description:
 *
 * @author sunjianfei
 */
public class RequestPool {

    public static final RequestPool gRequestPool = new RequestPool();

    private Hashtable<String, Observable<?>> mObservables;


    private RequestPool() {
    }

    public void init() {
        //1.初始化容器
        mObservables = new Hashtable<>();
    }


    public <T> Observable<HttpResponse<T>> request(RxOkHttpRequest<T> rxOkHttpRequest) {
        String requestId = rxOkHttpRequest.genRequestId();
        if (!mObservables.containsKey(requestId)) {
            Observable<HttpResponse<T>> observable = Observable.<HttpResponse<T>>create(subscriber -> {
                try {
                    //1.构建okHttp的request
                    Request request = rxOkHttpRequest.buildRequest();
                    Call call = RxOkHttp.sOkHttpClient.newCall(request);
                    //执行请求
                    Response response = call.execute();
                    HttpResponse<T> httpResponse = rxOkHttpRequest.parseNetworkResponse(response);
                    //2.发出事件
                    if (200 == httpResponse.status) {
                        subscriber.onNext(httpResponse);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new RxOkHttpError());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                } finally {
                    mObservables.remove(rxOkHttpRequest.genRequestId());
                }
            }).subscribeOn(Schedulers.computation());
            mObservables.put(requestId, observable);
            return observable;
        } else {
            return (Observable<HttpResponse<T>>) mObservables.get(requestId);
        }
    }
}

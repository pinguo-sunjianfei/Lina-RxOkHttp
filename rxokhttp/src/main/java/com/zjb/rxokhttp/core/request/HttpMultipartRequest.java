package com.zjb.rxokhttp.core.request;


import com.zjb.rxokhttp.bean.HttpParams;
import com.zjb.rxokhttp.core.HttpManager;
import com.zjb.rxokhttp.core.exception.AuthFailureError;

import okhttp3.RequestBody;

/**
 * time: 16/6/1
 * description: 上传文件对应的请求实体
 *
 * @author sunjianfei
 */
public class HttpMultipartRequest<T> extends HttpGsonRequest<T> {

    public HttpMultipartRequest(String url, HttpParams params) {
        super(Method.POST, url, params);
    }

    @Override
    public RequestBody getBody() throws AuthFailureError {
        RequestBody body = HttpManager.buildPostParams(mHttpParams);
        if (null == body) {
            return super.getBody();
        } else {
            return body;
        }
    }

}

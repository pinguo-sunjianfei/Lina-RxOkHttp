package com.zjb.rxokhttp.core.request;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.zjb.rxokhttp.bean.HttpParams;
import com.zjb.rxokhttp.bean.parser.BaseParser;
import com.zjb.rxokhttp.core.HttpManager;
import com.zjb.rxokhttp.core.exception.AuthFailureError;
import com.zjb.rxokhttp.core.response.HttpResponse;
import com.zjb.rxokhttp.log.HttpLogger;
import com.zjb.rxokhttp.utils.GsonUtil;
import com.zjb.rxokhttp.utils.RequestHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Response;


/**
 * time: 16/6/1
 * description: 网络请求返回的数据被封装到HttpResponse当中，这个类将HttpResponse当中的data封装到成一个T
 *
 * @author sunjianfei
 */
public class HttpGsonRequest<T> extends RxOkHttpRequest<T> {
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    protected HttpParams mHttpParams;

    protected String mNodeName;

    protected BaseParser<T> mParser;


    public HttpGsonRequest(String url) {
        super(url);
    }

    public HttpGsonRequest(String url, HttpParams params) {
        super(RequestHelper.buildUrl(url, params));
    }

    public Class<?> getClazz() {
        return null;
    }

    public HttpGsonRequest(String method, String url, HttpParams params) {
        super(url, method);
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(String method, BaseParser<T> parser, String url, HttpParams params) {
        super(url, method);
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }

    public HttpGsonRequest(String method, BaseParser<T> parser, String url, String mNodeName, HttpParams params) {
        super(url, method);
        this.mNodeName = mNodeName;
        this.mParser = parser;
        this.mHttpParams = params;
        if (Method.GET == method) {
            this.mUrl = HttpManager.buildGetURL(url, params, getParamsEncoding());
        }
    }


    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mHttpParams != null) {
            return mHttpParams.getTextParams();
        }
        return super.getParams();
    }

    @Override
    public MediaType getMediaType() {
        return MEDIA_TYPE_PLAIN;
    }

    @Override
    public HttpResponse<T> parseNetworkResponse(Response response) {
        HttpResponse<T> mResponse = new HttpResponse<>();
        if (null == response) {
            return mResponse;
        }
        String data = null;
        try {
            data = response.body().string();
        } catch (IOException e) {
            HttpLogger.e(e);
        }
        try {
            JSONObject result = new JSONObject(data);
            if (!TextUtils.isEmpty(mNodeName)) {
                if (result.has(mNodeName)) {
                    result = result.getJSONObject(mNodeName);
                }
            }
            mResponse.status = response.code();
            if (result.has("status")) {
                mResponse.status = result.optInt("status", HttpResponse.CODE_FAILED);
            }
            if (result.has("msg")) {
                mResponse.message = result.optString("msg", "");
            } else if (result.has("message")) {
                mResponse.message = result.optString("message", "");
            }

            if (result.has("serverTime")) {
                mResponse.serverTime = result.optDouble("serverTime");
            }
            //如果需要封装数据的nodeName不是"data"可以在此基础上扩展
            if (result.has("data")) {
                data = result.optString("data");
            }
            Class<?> clazz = getClazz();
            if (!TextUtils.isEmpty(data)) {
                //1.解析器首先解析
                if (mParser != null) {
                    mResponse.data = mParser.parser(data);
                } else if (clazz != null) {
                    if (String.class == clazz) {
                        mResponse.data = (T) data;
                    } else {
                        mResponse.data = GsonUtil.fromJson(data, (Class<T>) clazz);
                    }
                } else {
                    Type type = ((ParameterizedType) ((Object) this).getClass()
                            .getGenericSuperclass()).getActualTypeArguments()[0];
                    mResponse.data = GsonUtil.fromJson(data, type);
                }
            }
        } catch (JsonSyntaxException e) {
            HttpLogger.e("gson解析错误：" + e.getMessage());
        } catch (Exception e) {
        }
        return mResponse;
    }

    private void printJson(JSONObject obj) {
        try {
            Object o, object;
            JSONArray array;
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                o = obj.get(key);
                HttpLogger.e(key + ":" + o.toString() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

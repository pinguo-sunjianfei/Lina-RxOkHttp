package com.zjb.rxokhttp.core.request;

import com.zjb.rxokhttp.core.exception.AuthFailureError;
import com.zjb.rxokhttp.core.response.HttpResponse;
import com.zjb.rxokhttp.log.HttpLogger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * time:2016/5/31
 * description:
 *
 * @author sunjianfei
 */
public abstract class RxOkHttpRequest<T> {
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    protected Request.Builder builder = new Request.Builder();

    protected String mUrl;
    protected String mMethod;

    public RxOkHttpRequest(String url) {
        this.mUrl = url;
        this.mMethod = Method.GET;
    }

    public RxOkHttpRequest(String url, String method) {
        this.mUrl = url;
        this.mMethod = method;
    }

    public interface Method {
        String GET = "GET";
        String POST = "POST";
        String PUT = "PUT";
        String DELETE = "DELETE";
        String HEAD = "HEAD";
        String OPTIONS = "OPTIONS";
        String TRACE = "TRACE";
        String PATCH = "PATCH";
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    protected Map<String, String> getParams() throws AuthFailureError {
        return null;
    }

    protected Map<String, String> getHeaders() throws AuthFailureError {
        return Collections.emptyMap();
    }

    public abstract HttpResponse<T> parseNetworkResponse(Response response);

    public MediaType getMediaType() {
        return MEDIA_TYPE_STREAM;
    }

    public RequestBody getBody() throws AuthFailureError {
        return RequestBody.create(getMediaType(), encodeParameters(this.getParams(), this.getParamsEncoding()));
    }

    /**
     * 对Request生成一个唯一的id
     *
     * @return
     * @throws AuthFailureError
     */
    public String genRequestId() {
        StringBuilder builder = new StringBuilder(mUrl)
                .append(mMethod);
        try {
            //TODO 这里是有问题的,没想到好的解决方案,解决去重的问题
            builder.append(convertToHex(encodeParameters(this.getParams(), this.getParamsEncoding())));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        return builder.toString();
    }

    private static String convertToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHARS[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder encodedParams = new StringBuilder();
        try {
            Iterator uee = params.entrySet().iterator();
            boolean hasNext = uee.hasNext();
            while (hasNext) {
                Map.Entry entry = (Map.Entry) uee.next();
                encodedParams.append(URLEncoder.encode((String) entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) entry.getValue(), paramsEncoding));
                if (hasNext = uee.hasNext()) {
                    encodedParams.append('&');
                }
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    /**
     * 构建OkHttp的request
     *
     * @return
     * @throws AuthFailureError
     */
    public Request buildRequest() throws AuthFailureError {
        //1.添加请求头
        addHeaders();
        //2.添加请求体
        setConnectionParametersForRequest(getBody());
        return builder.url(mUrl).build();
    }

    private void addHeaders() {
        try {
            Headers.Builder headerBuilder = new Headers.Builder();
            Map<String, String> headers = new HashMap<>();
            headers.putAll(getHeaders());
            //添加gzip头部信息
            addGzipHeader(headers);
            for (String key : headers.keySet()) {
                headerBuilder.add(key, headers.get(key));
            }
            builder.headers(headerBuilder.build());
        } catch (Exception e) {
            HttpLogger.e(e);
        }
    }

    protected void addGzipHeader(Map<String, String> headers) {
        headers.put("Accept-Encoding", "gzip");
    }

    private void setConnectionParametersForRequest(RequestBody body) {
        switch (mMethod) {
            case RxOkHttpRequest.Method.GET:
                builder.get();
                break;
            case RxOkHttpRequest.Method.POST:
                builder.post(body);
                break;
            case RxOkHttpRequest.Method.PUT:
                builder.put(body);
                break;
            case RxOkHttpRequest.Method.DELETE:
                builder.delete();
                break;
            case RxOkHttpRequest.Method.HEAD:
                builder.head();
                break;
            case RxOkHttpRequest.Method.PATCH:
                builder.patch(body);
                break;
            default:
                throw new IllegalStateException("Unknown request method.");
        }
    }
}

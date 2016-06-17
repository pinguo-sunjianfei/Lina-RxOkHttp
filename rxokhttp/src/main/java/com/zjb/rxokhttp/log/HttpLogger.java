package com.zjb.rxokhttp.log;


import android.util.Log;

import com.zjb.rxokhttp.RxOkHttp;
import com.zjb.rxokhttp.core.request.RxOkHttpRequest;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;


/**
 * time: 16/5/31
 * description: http请求的日志管理类
 *
 * @author sunjianfei
 */
public class HttpLogger implements Interceptor {
    private static final String TAG = "Request";


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    /**
     * 请求发生之前，打印请求的相关信息
     *
     * @param request 请求
     */
    public static void logForRequest(Request request) {
        if (RxOkHttp.sDebug) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nHttp url : ").append(request.url().toString());
            String method;
            switch (request.method()) {
                case RxOkHttpRequest.Method.GET:
                    method = "GET";
                    break;
                case RxOkHttpRequest.Method.POST:
                    method = "POST";
                    break;
                case RxOkHttpRequest.Method.PUT:
                    method = "PUT";
                    break;
                case RxOkHttpRequest.Method.DELETE:
                    method = "DELETE";
                    break;
                case RxOkHttpRequest.Method.HEAD:
                    method = "HEAD";
                    break;
                case RxOkHttpRequest.Method.OPTIONS:
                    method = "OPTIONS";
                    break;
                case RxOkHttpRequest.Method.TRACE:
                    method = "TRACE";
                    break;
                case RxOkHttpRequest.Method.PATCH:
                    method = "PATCH";
                    break;
                default:
                    method = "GET";
            }

            builder.append("\nHttp method : ").append(method);
            try {
                Headers headers = request.headers();
                if (null != headers) {
                    StringBuilder headerBuilder = new StringBuilder();
                    headerBuilder.append("\nHttp headers: ");
                    builder.append(headers.toString());
                }
                RequestBody requestBody = request.body();
                if (requestBody != null) {
                    MediaType mediaType = requestBody.contentType();
                    if (null != mediaType) {
                        builder.append("\nRequestBody's contentType: " + mediaType.toString());
                        if (isText(mediaType)) {
                            builder.append("\nRequestBody's content : " + bodyToString(request));
                        } else {
                            e(TAG, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            e(builder.toString());
        }
    }

    /**
     * 打印返回的数据信息
     *
     * @param response
     * @return
     */
    private Response logForResponse(Response response) {
        if (RxOkHttp.sDebug) {
            try {
                Response.Builder builder = response.newBuilder();
                Response clone = builder.build();
                StringBuilder resultBuilder = new StringBuilder();

                resultBuilder.append("\nstatus:")
                        .append(clone.code())
                        .append("\nurl:")
                        .append(clone.request().url())
                        .append("\nmessage:")
                        .append(clone.message());

                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        resultBuilder.append("\nresponseBody's contentType:")
                                .append(mediaType.toString());
                        if (isText(mediaType)) {
                            String resp = body.string();
                            resultBuilder.append("\nresponseBody's content:")
                                    .append(resp);

                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            e(TAG, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            } catch (Exception e) {
                e(e);
            }
        }
        return response;
    }

    /**
     * 数据返回，尚未分发时的数据信息
     *
     * @param statusCode 状态码
     * @param url        请求连接
     * @param content    相应数据
     */
    public static void printResponse(int statusCode, String url, String content) {
        if (RxOkHttp.sDebug) {
            StringBuilder builder = new StringBuilder();
            builder.append("\nstatus:")
                    .append(statusCode)
                    .append("\nurl:")
                    .append(url)
                    .append("\ndata:")
                    .append(content);
            e(builder.toString());
        }
    }


    public static void e(String msg) {
        if (RxOkHttp.sDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (RxOkHttp.sDebug) {
            Log.e(tag, msg);
        }
    }

    public static void e(Exception e) {
        if (RxOkHttp.sDebug) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public static void e(Throwable tr) {
        if (RxOkHttp.sDebug) {
            Log.e(TAG, Log.getStackTraceString(tr));
        }
    }

    private static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }

    private static boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

}

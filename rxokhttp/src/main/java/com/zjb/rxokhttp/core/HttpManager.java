package com.zjb.rxokhttp.core;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.zjb.rxokhttp.bean.HttpParams;
import com.zjb.rxokhttp.utils.MimeTypeHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


/**
 * time: 16/6/17
 * description: 封装了http的基本请求
 *
 * @author sunjianfei
 */

public class HttpManager {
    public static final String BOUNDARY = "kmdleuhfpsidnl";
    private static final String MP_BOUNDARY = "--" + BOUNDARY;
    private static final String END_MP_BOUNDARY = MP_BOUNDARY + "--";
    private static final int BUFFER_SIZE = 8192;

    private HttpManager() {
    }

    public static String buildGetURL(String originUrl, HttpParams httpParams, String paramsEncoding) {
        Map<String, String> params = httpParams.getTextParams();
        if (params == null || params.isEmpty()) {
            return originUrl;
        }
        StringBuilder encodedParams = new StringBuilder(originUrl);
        if (!originUrl.endsWith("?")) {
            encodedParams.append("?");
        }
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
            return encodedParams.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    public static RequestBody buildPostParams(HttpParams params) {
        try {
            return buildParams(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static RequestBody buildParams(HttpParams params) throws Exception {
        //文本
        Map<String, String> text = params.getTextParams();
        //文件
        Map<String, Object> multi = params.getMutiParams();

        if (null != multi && !multi.isEmpty()) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder, text);

            Set<String> e = multi.keySet();
            Iterator<String> iterator = e.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = multi.get(key);
                RequestBody requestBody = null;
                String contentType = "application/octet-stream; charset=utf-8";
                if (value instanceof Bitmap) {
                    Bitmap stream = (Bitmap) value;
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    stream.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                    byte[] bytes = stream1.toByteArray();
                    requestBody = RequestBody.create(MediaType.parse(contentType), bytes);
                } else if (value instanceof File) {
                    File file = (File) value;
                    String mimeType = MimeTypeHelper.getContentType(file);
                    if (TextUtils.isEmpty(mimeType)) {
                        mimeType = "application/octet-stream; charset=utf-8";
                    }
                    contentType = mimeType;
                    requestBody = RequestBody.create(MediaType.parse(contentType), file);
                } else if (value instanceof ByteArrayOutputStream) {
                    ByteArrayOutputStream stream = (ByteArrayOutputStream) value;
                    requestBody = RequestBody.create(MediaType.parse(contentType), stream.toByteArray());
                }
                builder.addFormDataPart(key, key, requestBody);
            }
            return builder.build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder, text);
            return builder.build();
        }
    }

    private static void addParams(MultipartBody.Builder builder, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private static void addParams(FormBody.Builder builder, Map<String, String> params) {
        if (params != null) {
            for (String key : params.keySet()) {
                builder.add(key, params.get(key));
            }
        }
    }


}


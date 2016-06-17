package com.zjb.rxokhttp.core.cookie;


import com.zjb.rxokhttp.core.cookie.store.CookieStore;
import com.zjb.rxokhttp.core.cookie.store.HasCookieStore;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * time: 2016/5/31
 * description:
 *
 * @author sunjianfei
 */
public class CookieJarImpl implements CookieJar, HasCookieStore {
    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null.");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.get(url);
    }

    @Override
    public CookieStore getCookieStore() {
        return cookieStore;
    }
}

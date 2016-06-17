package com.zjb.rxokhttp.core.exception;

import android.content.Intent;


/**
 * time: 2016/5/31
 * description:
 *
 * @author sunjianfei
 */
public class AuthFailureError extends RxOkHttpError {
    private Intent mResolutionIntent;

    public AuthFailureError() {
        super();
    }

    public AuthFailureError(Intent intent) {
        this.mResolutionIntent = intent;
    }

    public AuthFailureError(String message) {
        super(message);
    }

    public AuthFailureError(String message, Exception reason) {
        super(message, reason);
    }

    public Intent getResolutionIntent() {
        return this.mResolutionIntent;
    }

    public String getMessage() {
        return this.mResolutionIntent != null ? "User needs to (re)enter credentials." : super.getMessage();
    }
}
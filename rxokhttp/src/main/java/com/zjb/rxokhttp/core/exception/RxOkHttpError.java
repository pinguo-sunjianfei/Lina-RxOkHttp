package com.zjb.rxokhttp.core.exception;


/**
 * time: 2016/5/31
 * description:
 *
 * @author sunjianfei
 */
public class RxOkHttpError extends Exception {

    public RxOkHttpError() {

    }

    public RxOkHttpError(String exceptionMessage) {
        super(exceptionMessage);
    }

    public RxOkHttpError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public RxOkHttpError(Throwable cause) {
        super(cause);
    }
}

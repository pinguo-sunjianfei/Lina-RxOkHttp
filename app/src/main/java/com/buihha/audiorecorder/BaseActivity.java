package com.buihha.audiorecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * time:2016/6/17
 * description:
 *
 * @author sunjianfei
 */
public class BaseActivity<ViewModel> extends AppCompatActivity {
    protected ViewModel mViewModel;
    protected CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    /**
     * 避免一些耗时的事件返回的回调在返回时出现NullPointException，需要把这些Subscription在生命周期结束时接触订阅关系，见onDestroy()中业务
     *
     * @param subscription
     */
    public void addSubscription(Subscription subscription) {
        if (subscription != null) {
            if (mCompositeSubscription == null || mCompositeSubscription.isUnsubscribed()) {
                mCompositeSubscription = new CompositeSubscription();
            }
            mCompositeSubscription.add(subscription);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //回收资源
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        if (mViewModel != null) {
            mViewModel = null;
        }
    }
}

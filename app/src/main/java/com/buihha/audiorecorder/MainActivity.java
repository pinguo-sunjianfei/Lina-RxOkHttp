package com.buihha.audiorecorder;

import android.os.Bundle;
import android.widget.Toast;

import com.buihha.audiorecorder.data.model.MainModel;

import rx.Subscription;

public class MainActivity extends BaseActivity<MainModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.start).setOnClickListener(v -> test());
    }

    public void test() {
        mViewModel = new MainModel();
        Subscription subscription = mViewModel.request()
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);
    }

    private void onNext(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void onError(Throwable e) {
        Toast.makeText(this, "妈个鸡,出错了", Toast.LENGTH_SHORT).show();
    }
}

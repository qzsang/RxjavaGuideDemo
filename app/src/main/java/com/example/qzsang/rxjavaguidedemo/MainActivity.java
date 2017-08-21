package com.example.qzsang.rxjavaguidedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    CompositeSubscription compositeSubscription = new CompositeSubscription();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

//    通过just生成Observer ()
    public void clickJust (View view) {
        final String Tag = "clickJust";
        Observable.just("qzsang","xiaowang","xiaoming")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {//onNext 全部执行完成后悔调用
                        log(Tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {//onNext执行错误时会调用   onError和onCompleted  有且只会执行一次
                        log(Tag, "onError:" + e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        log(Tag, "s：" + s);
                    }
                })
                .unsubscribe();//解除订阅
    }
//    通过from生成Observer（产生异常）
    public void clickFrom (View view) {
        final String Tag = "fromJust";
        String [] username = new String[] {"qzsang","xiaowang","xiaoming"};
        Observable.from(username)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {//onNext 全部执行完成后悔调用
                        log(Tag, "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {//onNext执行错误时会调用   onError和onCompleted  有且只会执行一次
                        log(Tag, "onError:" + e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        if(s.equalsIgnoreCase("xiaowang")) {//让其产生异常
                            int a = 1/0;
                        }
                        log(Tag, "s：" + s);

                    }
                })
                .unsubscribe();//解除订阅
    }


    public void clickFliter(View view) {
        final String Tag = "clickFliter";
        Observable.just("qzsang","xiaowang","xiaoming")
                .filter(new Func1<String, Boolean>() {//过滤掉xiaowang
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s) && !s.equalsIgnoreCase("xiaowang");
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(Tag, "s：" + s);
                    }
                })
                .unsubscribe();//解除订阅
    }


    public void clickMap(View view) {
        List<UserBean> userBeanList = new ArrayList<>();
        userBeanList.add(new UserBean("qzsang"));
        userBeanList.add(new UserBean("xiaowang"));
        userBeanList.add(new UserBean("xiaoming"));
        final String Tag = "clickMap";
        Observable.from(userBeanList)
                .map(new Func1<UserBean, String>() {
                    @Override
                    public String call(UserBean userBean) {
                        return userBean.name;
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(Tag, "s：" + s);
                    }
                })
                .unsubscribe();//解除订阅
    }


    public void clickFlatMap (View v) {
        List<UserBean> userBeanList = new ArrayList<>();
        userBeanList.add(new UserBean("qzsang"));
        userBeanList.add(new UserBean("xiaowang"));
        userBeanList.add(new UserBean("xiaoming"));
        final String Tag = "clickFlatMap";
        Observable.just(userBeanList, userBeanList)
                .flatMap(new Func1<List<UserBean>, Observable<UserBean>>() {
                    @Override
                    public Observable<UserBean> call(List<UserBean> list) {
                        return Observable.from(list);
                    }
                })
                .subscribe(new Action1<UserBean>() {
                    @Override
                    public void call(UserBean userBean) {
                        log(Tag, "userbean：" + userBean);
                    }
                })
                .unsubscribe();
    }

    public void clickThreadChange (View view) {

        final String Tag = "clickThreadChange";
        Subscription subscription = Observable.just("qzsang","xiaowang","xiaoming")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Toast.makeText(MainActivity.this,"处理的数据有：" + s, Toast.LENGTH_SHORT).show();
                        return s + ".so";
                    }
                })
                .observeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        log(Tag, "处理完成：" + s);
                    }
                });


        compositeSubscription.add(subscription);
    }


    private void log(String tag, String info) {
        Log.e(tag, info);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }
}

package com.example.qzsang.rxjavaguidedemo;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
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

    //需求： 上传图片并且通过返回url的显示img  （这个需求并不符合实际需求，但是我们通过这个需求来 使不使用rxjava两个代码的区别）

    //上传图片并且通过返回url的显示demo1
    public void clickDemo1(View v) {
        final File imgFile = new File("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i = 1/0;
                }catch (Exception e) {
                    e.printStackTrace();
                }

                File finalFile =compressImag(imgFile);

                final String url = upload(finalFile);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int i = 1/0;
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        showImag(url);
                    }
                });

            }
        }).start();
    }
    //上传图片并且通过返回url的显示demo2
    public void clickDemo2(View v) {
        File imgFile = new File("");
        Observable.just(imgFile)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<File, File>() {

                    @Override
                    public File call(File file) {
                        int i = 1/0;
                        return compressImag(file);
                    }
                })
                .map(new Func1<File, String>() {
                    @Override
                    public String call(File file) {
                        return upload(file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        int i = 1/0;
                        showImag(s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    void showImag(String url) {
        Toast.makeText(this,"图片已经显示",Toast.LENGTH_SHORT).show();
    }

    String upload (File file) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "url" + file.getAbsolutePath();
    }


    //压缩
    File compressImag(File file) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return file;
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
                });
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
                });
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
                });
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
                });
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
                });
    }

    Subscription subscription;
    public void clickThreadChange (View view) {

        final String Tag = "clickThreadChange";
        subscription = Observable.just("qzsang","xiaowang","xiaoming")
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

        log(Tag, "isUnsubscribed：" + subscription.isUnsubscribed());
        compositeSubscription.add(subscription);
    }

    public void clickTest (View view) {//源码解析为什么会自动取消订阅：  http://blog.csdn.net/jdsjlzx/article/details/51542003
        if (subscription == null) return;

        final String Tag = "clickTest";
        log(Tag, "isUnsubscribed：" + subscription.isUnsubscribed());//用于验证  其实在处理订阅中  如果完成  或者 发送错误  它将会自定解除订阅
    }

    private void log(String tag, String info) {
        Log.e(tag, info);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果在程序退出后还没出处理完成  就解除订阅
        if (compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }
}

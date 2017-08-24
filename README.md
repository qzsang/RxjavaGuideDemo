<br><br>

之前已经学了一遍rxjava  可是后来项目一直没有用到，开始渐渐忘记；<br>
所以，通过这个项目，从零开始，做个记录，这样，以后不管是复习，还是引入，都将大大减少废话时间。<br>
很详细的讲解可以看这篇文章：http://gank.io/post/560e15be2dca930e00da1083#toc_16 ,<br>
我写这个项目的主要目的，是->用最快的时间上手，如果你不想知道什么原理，只想知道怎么用，那这个demo很适合你。
<br>


* Rxjava是啥？
    * 一个基于观察者模式设计的一个框架，为了   1、线程切换更加方便；  2、代码解耦，提高可读性

* Rxjava怎么用？<br>
    * 既然是基于观察者，那就有  Observable (可观察者，即被观察者)、和  Observer (观察者)，  两者之间通过 subscribe (订阅)、事件建立连接。
    
        * 1、Observer （观察者） 怎么得到?
            ```java
                 Observer<String> observer = new Observer<String>() {
                                                    @Override
                                                    public void onNext(String s) {//观察执行时的方法
                                                        Log.d(tag, "Item: " + s);
                                                    }
                                    
                                                    @Override
                                                    public void onCompleted() {//执行完成时调用
                                                        Log.d(tag, "Completed!");
                                                    }
                                    
                                                    @Override
                                                    public void onError(Throwable e) {//执行时报错会调用的方法    onCompleted和onError  一定会调用其中一个
                                                        Log.d(tag, "Error!");
                                                    }
                                                };
            ```
        * 其实观察者 除了 Observer外 还有一个类，也是很常用的,即Subscriber  （Observer）的抽象实现类，相比Observer多了两个方法<br>
        onStart()：它会在 subscribe 刚开始，而事件还未发送之前被调用 ，可以用作流程开始前的初始化<br>
        unsubscribe():用来解除关系，避免内存泄漏<br>

  
    * 2、Observable（被观察者）怎么得到？<br>
        * a、通过 new，这个在实际使用中很少用到，就不举例了<br>
        * b、通过just(T...)
        ```java        
                    Observable observable = Observable.just("Hello", "Hi", "Aloha");
        ```
       * c、通过from(T[]) / from(Iterable<? extends T>)
        ```java
                        String[] words = {"Hello", "Hi", "Aloha"};
                        Observable observable = Observable.from(words);
        ```

    * 3、Subscribe (订阅) 怎建立关系?<br>
        ```java
                    observable.subscribe(observer);
                    // 或者：
                    observable.subscribe(subscriber);
        ```
        * 除了完整的回调 ,subscribe 也支持不完整回调,如下：
            ```java
                        Action1<String> onNextAction = new Action1<String>() {
                            // onNext()
                            @Override
                            public void call(String s) {
                                Log.d(tag, s);
                            }
                        };
                        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
                            // onError()
                            @Override
                            public void call(Throwable throwable) {
                                // Error handling
                            }
                        };
                        Action0 onCompletedAction = new Action0() {
                            // onCompleted()
                            @Override
                            public void call() {
                                Log.d(tag, "completed");
                            }
                        };
            
                        // 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
                        observable.subscribe(onNextAction);
                        // 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
                        observable.subscribe(onNextAction, onErrorAction);
                        // 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
                        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
            ```

    * 4、简单使用 （查看MainActivity 源码）

* Rxjava进阶 -  fliter 、map、 flatMap、Scheduler（线程切换）

    * fliter : 对观察者发出的值进行过滤，符合条件的才会下发；
    * map： 对分发的值进行一对一的转换，比如把id转化成bitmap；
    * flatmap： 与map类似，但是一对多的转换,比如讲users转化成user 并且继续分发下去 （相当多了个循环）
    * Scheduler： 用于控制线程的切换
        * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。<br>
        * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。<br>
        * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。<br>
        * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。<br>
        * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。<br><br>
        使用建议：计算的放Schedulers.computation()线程，其它的耗时操作放Schedulers.io()， 主线程的当然放AndroidSchedulers.mainThread()<br>

    * 有了这几个 Scheduler ，就可以使用 subscribeOn() 和 observeOn() 两个方法来对线程进行控制了。 * subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。 * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。

            ```java
                    Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .map(mapOperator) // 新线程，由 observeOn() 指定
                        .observeOn(Schedulers.io())
                        .map(mapOperator2) // IO 线程，由 observeOn() 指定
                        .observeOn(AndroidSchedulers.mainThread)
                        .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定
            ```
        * 如上：observeOn() 可以多次调用，程序实现了线程的多次切换，但是 subscribeOn() 只能调用一次的，不管位置放在哪里都可以。





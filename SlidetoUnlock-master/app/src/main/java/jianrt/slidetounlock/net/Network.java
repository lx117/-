package jianrt.slidetounlock.net;


import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 11719 on 2016/12/3.
 */

public class Network {
    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final Network INSTANCE = new Network();
    }
    //获取单例
    public static Network getInstance() {
        return Network.SingletonHolder.INSTANCE;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */

    private class HttpResultFunc<T> implements Func1<HttpJuHeResult<T>, T> {
        @Override
        public T call(HttpJuHeResult<T> httpResult) {
            if (httpResult.getError_code() != 0) {
                throw new ApiException(httpResult.getError_code());
            }
            return httpResult.getResult();
        }

    }


    private <T> Subscription toSubscribe(Observable<T> observable, Subscriber<T> subscriber) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    /**
     * 用于获取聚合笑话的数据
     *
     * @param subscriber 由调用者传过来的观察者对象
     * @param observable   被观察对象
     */

    public Subscription getJokesByHttpResultMap(Observable observable, Subscriber subscriber) {

        Observable newObservable = observable.map(new HttpResultFunc());
        return  toSubscribe(newObservable, subscriber);


    }
    /**
     * 用于获取聚合笑话的数据
     *
     * @param observable   被观察对象
     */

   /* public Observable getResultMap(Observable observable) {
        Observable newObservable = observable.map(new HttpResultFunc());
        return  observable.compose(new BaseSchedulerTransformer());


    }*/
}

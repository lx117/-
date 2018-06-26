package jianrt.slidetounlock.util;


import com.google.gson.Gson;
import com.socks.library.KLog;

import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import jianrt.slidetounlock.TbApplication;
import jianrt.slidetounlock.entity.CacheDb;
import jianrt.slidetounlock.greendao.service.CacheDbService;
import jianrt.slidetounlock.net.manager.RetrofitManager;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Description: RxUtil
 * Creator: yxc
 * date: 2016/9/21 18:47
 */
public class RxUtil {

    /**
     * 统一线程处理
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> rxSchedulerHelper() {    //compose简化线程
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 将网络获取的数据存储到数据库
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> rxCacheDb(final Type type) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> observable) {
                return observable
                        .doOnNext(new Action1<T>() {
                            @Override
                            public void call(T t) {
                                final String jsonObj = new Gson().toJson(t);
                                CacheDb cacheDb1 = new CacheDb();
                                CacheDb cacheDb = null;
                                if (!StringUtil.isEmpty(RetrofitManager.url)) {
                                    cacheDb = CacheDbService.queryData(RetrofitManager.url);
                                }
                                if (cacheDb != null) {
                                    cacheDb1.setId(cacheDb.getId());
                                }
                                cacheDb1.setUrl(RetrofitManager.url);
                                cacheDb1.setJsonObject(jsonObj);
                                CacheDbService.save(cacheDb1);
                            }
                        })
                        .onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
                            @Override
                            public Observable<? extends T> call(Throwable throwable) {
                                return CacheDbService.query(RetrofitManager.url)
                                        .map(new Func1<CacheDb, T>() {
                                            @Override
                                            public T call(CacheDb cacheDb) {
                                                return new Gson().fromJson(cacheDb.getJsonObject(), type);//new TypeToken<T>() {}.getType()
                                            }
                                        });
                            }
                        });
            }
        };
    }
    /**
     * 将网络获取的数据存储到数据库
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> rxCacheDb(final Type type, final String url) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> observable) {
                return observable
                        .doOnNext(new Action1<T>() {
                            @Override
                            public void call(T t) {
                                final String jsonObj = new Gson().toJson(t);
                                CacheDb cacheDb1 = new CacheDb();
                                CacheDb cacheDb = null;
                                if (!StringUtil.isEmpty(url)) {
                                    cacheDb = CacheDbService.queryData(url);
                                }
                                if (cacheDb != null) {
                                    cacheDb1.setId(cacheDb.getId());
                                }
                                cacheDb1.setUrl(url);
                                cacheDb1.setJsonObject(jsonObj);
                                CacheDbService.save(cacheDb1);
                            }
                        })
                        .onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
                            @Override
                            public Observable<? extends T> call(Throwable throwable) {
                                return CacheDbService.query(url)
                                        .map(new Func1<CacheDb, T>() {
                                            @Override
                                            public T call(CacheDb cacheDb) {
                                                return new Gson().fromJson(cacheDb.getJsonObject(), type);//new TypeToken<T>() {}.getType()
                                            }
                                        });
                            }
                        });
            }
        };
    }
    /**
     * 统一返回结果处理
     *
     * @param <T>
     * @return
     */
    /*public static <T> Observable.Transformer<VideoHttpResponse<T>, T> handleResult() {   //compose判断结果
        return new Observable.Transformer<VideoHttpResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<VideoHttpResponse<T>> httpResponseObservable) {
                return httpResponseObservable.flatMap(new Func1<VideoHttpResponse<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(VideoHttpResponse<T> videoHttpResponse) {
                        if (videoHttpResponse.getCode() == 200) {
                            return createData(videoHttpResponse.getRet());
                        } else if (!TextUtils.isEmpty(videoHttpResponse.getMsg())) {
                            return Observable.error(new ApiException("*" + videoHttpResponse.getMsg()));
                        } else {
                            return Observable.error(new ApiException("*" + "服务器返回error"));
                        }
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<GankHttpResponse<T>, T> handleGankResult() {   //compose判断结果
        return new Observable.Transformer<GankHttpResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<GankHttpResponse<T>> httpResponseObservable) {
                return httpResponseObservable.flatMap(new Func1<GankHttpResponse<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(GankHttpResponse<T> tGankHttpResponse) {
                        if(!tGankHttpResponse.getError()) {
                            return createData(tGankHttpResponse.getResults());
                        } else {
                            return Observable.error(new ApiException("服务器返回error"));
                        }
                    }
                });
            }
        };
    }*/

    /**
     * 生成Observable
     *
     * @param <T>
     * @return
     */
    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                e.onNext(t);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
        /*return RxJavaInterop.toV2Flowable(SerializedSubject.create(new SerializedSubject.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }));*/
    }
    /**
     * 生成Observable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createObsData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
    public static <T> SerializedSubject<T,T> createSerializedData(final T t) {
        SerializedSubject subject = new SerializedSubject(PublishSubject.create());
        try {
            subject.onNext(t);
            subject.onCompleted();
        } catch (Exception e) {
            subject.onError(e);
        }
        return  subject;
    }

    private <T> Func1<Throwable, ? extends Observable<? extends T>> refreshTokenAndRetry(final Observable<T> toBeResumed) {
        return new Func1<Throwable, Observable<? extends T>>() {
            @Override
            public Observable<? extends T> call(Throwable throwable) {
                throwable.printStackTrace();
                if (throwable instanceof HttpException) {
                    switch (((HttpException) throwable).code()) {
                        case 403:
                            KLog.e("没有权限访问此链接！");
                            break;
                        case 504:
                            if (!NetUtil.isConnected(TbApplication.getInstance())) {
                                KLog.e("没有联网哦！");
                            } else {
                                KLog.e("网络连接超时！");
                            }
                            break;
                        default:
                            break;
                    }
                    return toBeResumed;
                }
                // re-throw this error because it's not recoverable from here
                return Observable.error(throwable);
            }

        };
    }
}

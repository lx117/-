package jianrt.slidetounlock.greendao.service;


import org.greenrobot.greendao.rx.RxQuery;

import jianrt.slidetounlock.entity.CacheDb;
import jianrt.slidetounlock.greendao.GreenDaoManager;
import jianrt.slidetounlock.greendao.gen.CacheDbDao;
import rx.Observable;

/**
 * Author:11719<p>
 * CreateDate:2016/12/29<p>
 * Fuction:<p>
 */

public class CacheDbService {
    private static CacheDbDao getCacheDbDao() {
        return GreenDaoManager.getInstance().getmDaoSession().getCacheDbDao();
    }

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static Observable<CacheDb> saveData(CacheDb cacheDb) {
        return getCacheDbDao().rx().insert(cacheDb);
    }

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static long save(CacheDb cacheDb) {
        return getCacheDbDao().insertOrReplace(cacheDb);
    }

    /**
     * @desc 添加数据至数据库，如果存在，将原来的数据覆盖
     **/
    public static CacheDb queryData(String url) {
        return  getCacheDbDao().queryBuilder().where(CacheDbDao.Properties.Url.eq(url)).unique();
    }

    /**
     * @desc 查询数据
     **/
    public static Observable<CacheDb> query(String url) {
        RxQuery<CacheDb> rxQuery = getCacheDbDao().queryBuilder().where(CacheDbDao.Properties.Url.eq(url)).rx();

        return rxQuery.oneByOne();
    }
}

package jianrt.slidetounlock.net;

import jianrt.slidetounlock.entity.AppTrustList;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by 11719 on 2016/12/3.
 */

public interface HttpService {

    @GET("AppTrustList.txt")
    Observable<AppTrustList> getAppTrustList();
}

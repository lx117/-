package jianrt.slidetounlock.net;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ClassName: HostType<p>
 * Author:
 * Fuction: 请求数据host的类型<p>
 * CreateDate:<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public class HostType {

    /**
     * 多少种Host类型
     */
    public static final int TYPE_COUNT = 2;

    /**
     * HTTP host
     */
    //@HostTypeChecker
    public static final int HTTP_TYPE = 1;

    /**
     * WebSocket host
     */
    //@HostTypeChecker
    public static final int WS_TYPE = 2;

    /**
     * HTTP other host
     */
    //@HostTypeChecker
    public static final int HTTP_OTHER_TYPE = 3;

    /**
     * 替代枚举的方案，使用IntDef保证类型安全
     */
    @IntDef({HTTP_TYPE, WS_TYPE ,HTTP_OTHER_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HostTypeChecker {

    }

}

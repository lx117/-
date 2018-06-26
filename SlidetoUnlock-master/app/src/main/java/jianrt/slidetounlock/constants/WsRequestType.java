package jianrt.slidetounlock.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:11719<p>
 * CreateDate:2016/12/27<p>
 * Fuction:<p>
 */

public class WsRequestType {
    /**
     * ws断开连接
     */
    public static final int DISCONNECT = 0;
    /**
     * ws连接成功
     */
    public static final int CONNECT = 1;
    /**
     * ws接受BINARY成功
     */
    public static final int MESSAGE_BINARY = 2;
    /**
     * ws接受STRING成功
     */
    public static final int MESSAGE_STRING = 3;
    /**
     * 替代枚举的方案，使用IntDef保证类型安全
     */
    @IntDef({DISCONNECT,CONNECT,MESSAGE_BINARY,MESSAGE_STRING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WsRequestTypeChecker{

    }
}

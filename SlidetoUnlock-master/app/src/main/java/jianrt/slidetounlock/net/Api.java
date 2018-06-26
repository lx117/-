package jianrt.slidetounlock.net;

/**
 * ClassName:<p>
 * Author:<p>
 * Fuction: 请求接口<p>
 * CreateDate:<p>
 * UpdateUser:<p>
 * UpdateDate:<p>
 */
public class Api {

    /**
     * http URL
     */
    public static final String HTTP_HOST = "http://gank.io/api/";//http://app.121tongbu.com/  ; http://www.zhuangbi.info/

    /**
     * Websocket URL
     */
    public static final String WS_HOST = "ws://dev-ws.121tongbu.com/wsintf";//ws://10.0.0.42:5000/wsintf

    /**
     * http other URL
     */
    public static final String HTTP_HOST_OTHER = "http://www.121tongbu.com/";

    /**
     * 获取对应的host
     *
     * @param hostType host类型
     * @return host
     */
    public static String getHost(int hostType) {
        switch (hostType) {
            case HostType.HTTP_TYPE:
                return Api.HTTP_HOST;
            case HostType.WS_TYPE:
                return Api.WS_HOST;
            case HostType.HTTP_OTHER_TYPE:
                return Api.HTTP_HOST_OTHER;
        }
        return "";
    }

}

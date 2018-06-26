package jianrt.slidetounlock;

import android.app.Application;
import android.content.Context;

import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.socks.library.KLog;

import jianrt.slidetounlock.broadcast.AppReceiver;
import jianrt.slidetounlock.broadcast.Receiver2;
import jianrt.slidetounlock.greendao.GreenDaoManager;
import jianrt.slidetounlock.service.AppService;
import jianrt.slidetounlock.service.Service2;


/**
 * Created by 11719 on 2016/11/17.
 */

public class TbApplication extends Application {

    private static boolean DEVELOPER_MODE=true;
    private static TbApplication instance;
    private DaemonClient mDaemonClient;
    public static TbApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        KLog.init(BuildConfig.DEBUG);
        GreenDaoManager.getInstance();

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mDaemonClient = new DaemonClient(createDaemonConfigurations());
        mDaemonClient.onAttachBaseContext(base);
    }
    private DaemonConfigurations createDaemonConfigurations(){
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "jianrt.slidetounlock:process1",
                AppService.class.getCanonicalName(),
                AppReceiver.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "jianrt.slidetounlock:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());
        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}

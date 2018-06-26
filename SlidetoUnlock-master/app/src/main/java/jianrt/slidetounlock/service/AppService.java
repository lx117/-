package jianrt.slidetounlock.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.socks.library.KLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jianrt.slidetounlock.TbApplication;
import jianrt.slidetounlock.constants.Constant;
import jianrt.slidetounlock.entity.AppTrustList;
import jianrt.slidetounlock.entity.CacheDb;
import jianrt.slidetounlock.greendao.service.CacheDbService;
import jianrt.slidetounlock.util.AppUtils;
import jianrt.slidetounlock.util.ScreenListener;
import jianrt.slidetounlock.util.ShellUtils;
import jianrt.slidetounlock.util.StringUtil;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AppService extends Service {
    public AppService() {
    }
    List<String> systemList;
    DevicePolicyManager manager;
    Subscription subscription;
    ScreenListener screenListener;

    public static ButtonStateListener buttonStateListener;
    public interface ButtonStateListener {// 返回给调用者屏幕状态信息
        public void onButtonOn(boolean isToast);

        public void onButtonOff(boolean isToast);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //flags = Service.START_STICKY;
        KLog.e("onStartCommand");
        /*Notification notification = new Notification();   //此处为创建前台服务，但是通知栏消息为空，这样我们就可                              以在不通知用户的情况下启动前台服务了
        startForeground(1, notification);*/
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //获取系统应用列表
        systemList = AppUtils.getSystemAppInfo(TbApplication.getInstance());

        //开启系统权限
        manager= (DevicePolicyManager) getApplication().getSystemService(getApplication().DEVICE_POLICY_SERVICE);

        //开启检测服务
        openwork(systemList);

        //开启屏幕监听
        openlistener();
    }

    /**
     * 开启屏幕监听
     */
    private void openlistener() {
        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                KLog.e("onUserPresent", "onUserPresent");
                if (!AppUtils.isWorked("jianrt.slidetounlock.service.AppService")) {
                    getApplication().startService(new Intent(getApplication(), AppService.class));
                }
            }

            @Override
            public void onScreenOn() {
                KLog.e("onScreenOn", "onScreenOn");
            }

            @Override
            public void onScreenOff() {
                KLog.e("onScreenOff", "onScreenOff");
            }
        });
    }
    //判断是否设置了锁屏密码
    private boolean checkPasswordToUnLock(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            KeyguardManager keyguardManager = (KeyguardManager) TbApplication.getInstance().getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        }else{
            return isSecured();
        }
    }
    private boolean isSecured(){
        boolean isSecured = false;
        String classPath = "com.android.internal.widget.LockPatternUtils";
        try{
            Class<?> lockPatternClass = Class.forName(classPath);
            Object lockPatternObject = lockPatternClass.getConstructor(Context.class).newInstance(getApplicationContext());
            Method method = lockPatternClass.getMethod("isSecure");
            isSecured = (boolean) method.invoke(lockPatternObject);
        }catch (Exception e){
            isSecured = false;
        }
        return isSecured;
    }

    /**
     * 开启检测第三方不信任程序服务
     * @param systemList 系统应用列表
     */
    private void openwork(final List<String> systemList) {
        if(buttonStateListener!=null)
            buttonStateListener.onButtonOn(true);
        if(subscription!=null){
            subscription.unsubscribe();
        }
        subscription = Observable.interval(0,2, TimeUnit.SECONDS)
                .onBackpressureDrop()
                .observeOn(Schedulers.io())
                .map(new Func1<Long, AppTrustList>() {
                    @Override
                    public AppTrustList call(Long aLong) {
                        CacheDb cacheDb = CacheDbService.queryData(Constant.APP_LIST_INFO);
                        if(cacheDb==null) {
                            KLog.e("onErrorResumeNext");
                            String s = StringUtil.getFromAssets(TbApplication.getInstance(),"AppTrustList.txt");
                            JsonObject out = new JsonParser().parse(s).getAsJsonObject();
                            return new Gson().fromJson(out, new TypeToken<AppTrustList>(){}.getType());
                        }else{
                            KLog.e("map");
                            return new Gson().fromJson(cacheDb.getJsonObject(), new TypeToken<AppTrustList>(){}.getType());//new TypeToken<T>() {}.getType()
                        }
                    }
                })
                .filter(new Func1<AppTrustList, Boolean>() {//过滤数据
                    @Override
                    public Boolean call(AppTrustList appTrustList) {
                        //白名单
                        List<String> strings = appTrustList.getList();
                        strings.addAll(systemList);

                        String topApp = AppUtils.getTopActivty(getApplication());
                        if((!StringUtil.isEmpty(topApp)&&!strings.contains(topApp))||AppUtils.getBlackList().contains(topApp)){
                            List<String> commands = new ArrayList<>();
                            //commands.add("pm uninstall "+AppUtils.getTopActivty(getApplication()));//静默卸载
                            //commands.add("pm enable "+AppUtils.getTopActivty(getApplication()));//解冻
                            commands.add("pm disable "+topApp);//冻结
                            //commands.add("pm list packages -d");//查看禁用列表
                            if (ShellUtils.checkRootPermission()) {
                                KLog.e("pm disable "+AppUtils.getTopActivty(getApplication()));
                                ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
                            }else{
                                KLog.e("请先root");
                            }
                        }
                        return AppUtils.isAvilibles(getApplication(),strings);
                    }
                })
                /*.takeUntil(new Func1<AppTrustList, Boolean>() {//满足条件则终止轮询
                    @Override
                    public Boolean call(AppTrustList appTrustList) {
                        List<String> strings = appTrustList.getList();
                        strings.addAll(systemList);
                        return AppUtils.isAvilibles(getApplication(),strings);
                    }
                })*/
                .subscribe(new Subscriber<AppTrustList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.e(e);
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            try {
                                if(!AppUtils.isNoSwitch()) {
                                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                                    return;
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        openwork(systemList);
                    }

                    @Override
                    public void onNext(AppTrustList appTrustList) {
                        if(StringUtil.isEmpty(appTrustList.getPassword())) {
                            String s = StringUtil.getFromAssets(TbApplication.getInstance(), "AppTrustList.txt");
                            JsonObject out = new JsonParser().parse(s).getAsJsonObject();
                            AppTrustList appTrust = new Gson().fromJson(out, new TypeToken<AppTrustList>() {
                            }.getType());
                            appTrustList.setPassword(appTrust.getPassword());
                        }
                        try {
                            //boolean res =  manager.resetPassword(appTrustList.getPassword(), 0);//给手机设置密码
                            //KLog.e(res+"  password="+appTrustList.getPassword());
                            Constant.APP_PASSWORD = appTrustList.getPassword();
                        }catch (Exception e){
                            //manager.lockNow();
                        }
                        //manager.lockNow();
                    }
                });
    }

    /**
     * 关闭检测
     */
    private void closework(){
        screenListener.unregisterListener();
        subscription.unsubscribe();
        if(buttonStateListener!=null)
            buttonStateListener.onButtonOff(false);
    }
    @Override
    public void onDestroy() {
        closework();
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

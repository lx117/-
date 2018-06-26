package jianrt.slidetounlock.util;

/**
 * Author:11719<p>
 * CreateDate:2017/2/28<p>
 * Fuction:<p>
 */
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.socks.library.KLog;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import jianrt.slidetounlock.TbApplication;

/**
 * 类描述：
 * Created by lizhenya on 16/8/16.
 */
public class AppUtils {

    private AppUtils() {
        throw new UnsupportedOperationException("can not be instantiated!");
    }

    /**
     * 方法描述：根据路径安装App
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        installApp(context, new File(filePath));
    }

    /**
     * 方法描述：根据文件安装App
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void installApp(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 方法描述:卸载指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApp(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    /*
    * m命令可以通过adb在shell中执行，同样，我们可以通过代码来执行
    */
    public static String execCommand(String ...command)  {
        Process process=null;
        InputStream errIs=null;
        InputStream inIs=null;
        String result="";

        try {
            process=new ProcessBuilder().command(command).start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs=process.getErrorStream();
            while((read=errIs.read())!=-1){
                baos.write(read);
            }
            inIs=process.getInputStream();
            while((read=inIs.read())!=-1){
                baos.write(read);
            }
            result=new String(baos.toByteArray());
            if(inIs!=null)
                inIs.close();
            if(errIs!=null)
                errIs.close();
            process.destroy();
        } catch (IOException e) {
            result = e.getMessage();
        }
        return result;
    }
    /**
     * 封装App信息的Bean类
     */
    public static class AppInfo {

        private String name;
        private Drawable icon;
        private String packageName;
        private String versionName;
        private int versionCode;
        private boolean isSD;
        private boolean isUser;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public boolean isSD() {
            return isSD;
        }

        public void setSD(boolean SD) {
            isSD = SD;
        }

        public boolean isUser() {
            return isUser;
        }

        public void setUser(boolean user) {
            isUser = user;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packagName) {
            this.packageName = packagName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        /**
         * @param name        名称
         * @param icon        图标
         * @param packageName 包名
         * @param versionName 版本号
         * @param versionCode 版本Code
         * @param isSD        是否安装在SD卡
         * @param isUser      是否是用户程序
         */
        public AppInfo(String name, Drawable icon, String packageName,
                       String versionName, int versionCode, boolean isSD, boolean isUser) {
            this.setName(name);
            this.setIcon(icon);
            this.setPackageName(packageName);
            this.setVersionName(versionName);
            this.setVersionCode(versionCode);
            this.setSD(isSD);
            this.setUser(isUser);
        }

    }

    /**
     * 方法描述:获取当前App信息AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序)
     *
     * @param context 上下文
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi != null ? getBean(pm, pi) : null;
    }

    /**
     * 方法描述:得到AppInfo的Bean
     *
     * @param pm 包的管理
     * @param pi 包的信息
     * @return AppInfo类
     */
    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
        ApplicationInfo ai = pi.applicationInfo;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packageName = pi.packageName;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSD = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
        boolean isUser = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
        return new AppInfo(name, icon, packageName, versionName, versionCode, isSD, isUser);
    }

    /**
     * 方法描述:获取所有已安装App信息
     * AppInfo（名称，图标，包名，版本号，版本Code，是否安装在SD卡，是否是用户程序)
     * <p>依赖上面的getBean方法
     *
     * @param context 上下文
     * @return 所有已安装的AppInfo列表
     */
    public static List<AppInfo> getAllAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if (pi != null) {
                list.add(getBean(pm, pi));
            }
        }
        return list;
    }

    /**
     * 方法描述:根据包名获取意图
     *
     * @param context     上下文
     * @param packageName 包名
     * @return 意图
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /**
     * 方法描述:根据包名判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true: 已安装<br>false: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return getIntentByPackageName(context, packageName) != null;
    }

    /**
     * 方法描述:打开指定包名的App
     *
     * @param context     上下文
     * @param packageName 包名
     * @return true: 打开成功<br>false: 打开失败
     */
    public static boolean openAppByPackageName(Context context, String packageName) {
        Intent intent = getIntentByPackageName(context, packageName);
        if (intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 方法描述:打开指定包名的App应用信息界面
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void openAppInfo(Context context, String packageName) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    /**
     * 方法描述:可用来做App信息分享
     *
     * @param context 上下文
     * @param info    分享信息
     */
    public static void shareAppInfo(Context context, String info) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, info);
        context.startActivity(intent);
    }

    /**
     * 方法描述:判断当前App处于前台还是后台
     * <p>需添加权限 android.permission.GET_TASKS</p>
     * <p>并且必须是系统应用该方法才有效</p>
     *
     * @param context 上下文
     * @return true: 后台<br>false: 前台
     */
    public static boolean isAppBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 检查手机允许安装的软件列表
     * @param context
     ** @param packageNameList 信任列表
     * @return
     */
    public static boolean isAvilibles(Context context, List<String> packageNameList) {
        /*for(String packageName:getThirdAppInfo(context)){
            if(!packageNameList.contains(packageName)){
                return getAppSatus(context,packageNameList);
                *//*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    KLog.e("LOLLIPOP");
                    return isAppInactive(context, packageName);
                }else{
                    KLog.e("LESS LOLLIPOP");
                    return getAppSatus(context,packageName);
                }*//*
            }
        }
        return false;*/
        //return  getAppSatus(context,packageNameList);
        if(StringUtil.isEmpty(getTopActivty(context)) || packageNameList.contains(getTopActivty(context))){
            KLog.e("false " + getTopActivty(context));
            return false;
        }else{
            KLog.e("true " + getTopActivty(context));
            return true;
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return  getAppSatus(context,packageNameList);
        }else {
            if (StringUtil.isEmpty(getLollipopRecentTask(context)) || packageNameList.contains(getLollipopRecentTask(context))) {
                KLog.e("false " + getLollipopRecentTask(context));
                return false;
            } else {
                KLog.e("true " + getLollipopRecentTask(context));
                return true;
            }
        }*/
    }

    public static boolean isAppInactive(Context context, String pageName){
        UsageStatsManager m=(UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(m.isAppInactive(pageName)){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    //获取到栈顶应用程序的包名
    public static String getTopActivty(Context context) {
        String topPackageName="";
        //android5.0以上获取方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();

            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }

        }
        //android5.0以下获取方式
        else{
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
            topPackageName = taskInfo.topActivity.getPackageName();
        }
        return topPackageName;
    }
    public static String getLollipopRecentTask(Context context) {
        final int PROCESS_STATE_TOP = 2;
        try {
            //通过反射获取私有成员变量processState，稍后需要判断该变量的值
            Field processStateField = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
            List<ActivityManager.RunningAppProcessInfo> processes = ((ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                //判断进程是否为前台进程
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    int state = processStateField.getInt(process);
                    //processState值为2
                    if (state == PROCESS_STATE_TOP) {
                        String[] packname = process.pkgList;
                        return packname[0];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 返回app运行状态
     * 1:程序在前台运行
     * 2:程序在后台运行
     * 3:程序未启动
     * 注意：需要配置权限<uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean getAppSatus(Context context, List<String> packageNameList) {

        //获取运行中进程
        List<ProcessManager.Process> runningProcesses = ProcessManager.getRunningProcesses();
        List<ProcessManager.Process> Processes=new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        for (ProcessManager.Process runningProcesse : runningProcesses) {
            String packname = runningProcesse.getPackageName();
            try {
                ApplicationInfo app = pm.getApplicationInfo(packname, 0);
                    //非系统程序
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        //thirdAppList.add(app);
                        Processes.add(runningProcesse);
                    }
                    //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
                    else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                        //thirdAppList.add(app);
                        Processes.add(runningProcesse);
                    }
                /*if (applicationInfo.packageName!=null && applicationInfo.packageName.equals(pageName)) {
                    return 0;
                }*/
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }
        for (ProcessManager.Process process:Processes) {
            if(process.getPackageName()!=null && !packageNameList.contains(process.getPackageName())){
                return true;
            }
        }
        return false;
    }

    //判断是否开启允许查看使用情况的应用
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) TbApplication.getInstance()
                .getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    //获取第三方应用信息
    public static ArrayList<String> getThirdAppInfo(Context context) {
        List<ApplicationInfo> appList = getInstallAppInfo(context);
        List<ApplicationInfo> thirdAppList = new ArrayList<ApplicationInfo>();
        thirdAppList.clear();
        for (ApplicationInfo app : appList) {
            //非系统程序
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                thirdAppList.add(app);
            }
            //本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
            else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                thirdAppList.add(app);
            }
        }
        PackageManager mypm = context.getPackageManager();
        ArrayList<String> thirdAppNameList = new ArrayList<String>();
        for(ApplicationInfo app : thirdAppList) {
            thirdAppNameList.add((String)app.packageName);
        }

        return thirdAppNameList;
    }

    //获取所有应用列表
    public static List<ApplicationInfo> getInstallAppInfo(Context context) {
        PackageManager mypm = context.getPackageManager();
        List<ApplicationInfo> appInfoList = mypm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(appInfoList, new ApplicationInfo.DisplayNameComparator(mypm));// 排序

        for(ApplicationInfo app: appInfoList) {
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app label = " + (String)app.loadLabel(umpm));
            //Log.v(LogTag, "RunningAppInfoParam  getInstallAppInfo app packageName = " + app.packageName);
        }

        return appInfoList;
    }

    //获取系统应用信息
    public static ArrayList<String> getSystemAppInfo(Context context) {
        List<ApplicationInfo> appList = getInstallAppInfo(context);
        List<ApplicationInfo> sysAppList = new ArrayList<ApplicationInfo>();
        sysAppList.clear();
        for (ApplicationInfo app : appList) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                sysAppList.add(app);
            }
        }
        PackageManager mypm = context.getPackageManager();
        ArrayList<String> sysAppNameList = new ArrayList<String>();
        for(ApplicationInfo app : sysAppList) {
            sysAppNameList.add(app.packageName);
        }

        return sysAppNameList;

    }

    //网络黑名单
    public static List<String> getWifiBlackList(){
        List<String> blackList = new ArrayList<String>();
        blackList.add("com.miui.video");//视频
        blackList.add("com.xiaomi.market");//应用商店
        return blackList;
    }
    //系统应用黑名单
    public static List<String> getBlackList(){
        List<String> blackList = new ArrayList<String>();
        //blackList.add("com.android.packageinstaller");
        blackList.add("com.xiaomi.jr");
        blackList.add("com.wali.live");
        blackList.add("com.mi.liveassistant");
        blackList.add("com.miui.klo.bugreport");
        blackList.add("com.google.android.marvin.talkback");
        blackList.add("me.twrp.twrpapp");
        blackList.add("com.android.updater");//系统更新
        blackList.add("com.android.fileexplorer");//文件夹
        blackList.add("com.miui.notes");//便签
        blackList.add("com.android.browser");//浏览器
        blackList.add("com.xiaomi.payment");//米币支付
        blackList.add("com.android.noisefield");//泡泡
        blackList.add("com.miui.screenrecorder");//屏幕录制
        blackList.add("com.android.calendar");//日历
        blackList.add("com.miui.powerkeeper");//神隐模式
        //blackList.add("com.miui.video");//视频
        blackList.add("com.android.providers.downloads.ui");
        blackList.add("com.xiaomi.account");//我的小米
        blackList.add("com.miui.fm");//收音机
        blackList.add("com.android.quicksearchbox");//搜索
        //blackList.add("com.android.providers.downloads");//下载管理程序
        blackList.add("com.miui.gallery");//相册
        //blackList.add("com.android.camera");//相机
        blackList.add("com.miui.cloudservice");//小米云服务
        blackList.add("com.android.midrive");//小米云盘
        blackList.add("com.xiaomi.mitunes");//小米助手
        blackList.add("com.miui.player");//音乐
        blackList.add("com.android.musicvis");//音乐可视化壁纸
        blackList.add("com.xiaomi.market");//应用商店
        blackList.add("com.intel.vpp");//英特尔智能视频
        blackList.add("com.miui.bugreport");//用户反馈
        blackList.add("com.xiaomi.gamecenter.sdk.service");//游戏服务
        blackList.add("com.xiaomi.gamecenter.pad");//游戏中心
        blackList.add("com.android.contacts");//联系人
        blackList.add("com.android.email");//电子邮箱
        blackList.add("com.miui.weather2");//天气
        blackList.add("com.miui.calculator");//计算器
        blackList.add("com.android.deskclock");//时钟
        //blackList.add("com.android.settings");
        return blackList;
    }


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }
    public static Process run(String command) throws IOException {
        /*String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
        String DIR_APP = DIR_SDCARD + "/Android/data/" + context.getPackageName() + "/dir/";*/
        /*List<String> strings = new ArrayList<>();
        strings.add("su");
        strings.add("-c");
        strings.add(command);
        ProcessBuilder builder = new ProcessBuilder(strings);*/
        // ProcessBuilder builder = new ProcessBuilder("su");
        ProcessBuilder builder = new ProcessBuilder("su");
                /*.command(command)
                .redirectErrorStream(true);*/
        // Process p = Runtime.getRuntime().exec("su");
        Process p = builder.start(); // Error Line
        DataOutputStream dos = new DataOutputStream(p.getOutputStream());

        dos.writeBytes(command + "\n");
        dos.flush();
        dos.writeBytes("exit\n");
        dos.flush();
        return p;
    }
    public static boolean RootCommand(String command){
        Process process = null;
        DataOutputStream os = null;
        try{
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e){
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally{
            try{
                if (os != null){
                    os.close();
                }
                process.destroy();
            } catch (Exception e){
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }



    public static boolean isWorked(String className) {
        ActivityManager myManager = (ActivityManager) TbApplication.getInstance()
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(60);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }
}

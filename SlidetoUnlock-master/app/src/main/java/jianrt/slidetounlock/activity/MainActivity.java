package jianrt.slidetounlock.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.SpaceDecoration;
import com.socks.library.KLog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidwall.DroidWallActivity;
import io.reactivex.functions.Consumer;
import jianrt.slidetounlock.R;
import jianrt.slidetounlock.TbApplication;
import jianrt.slidetounlock.adapter.InfoAdapter;
import jianrt.slidetounlock.constants.Constant;
import jianrt.slidetounlock.entity.AppTrustList;
import jianrt.slidetounlock.entity.CacheDb;
import jianrt.slidetounlock.greendao.service.CacheDbService;
import jianrt.slidetounlock.net.HostType;
import jianrt.slidetounlock.net.HttpService;
import jianrt.slidetounlock.net.manager.RetrofitManager;
import jianrt.slidetounlock.service.AppService;
import jianrt.slidetounlock.util.AppUtils;
import jianrt.slidetounlock.util.RxUtil;
import jianrt.slidetounlock.util.ShellUtils;
import jianrt.slidetounlock.util.StringUtil;
import jianrt.slidetounlock.view.WaitDialog;
import rx.functions.Action1;

public class MainActivity extends Activity implements AppService.ButtonStateListener {
    @BindView(R.id.OpenOrClose)
    TextView OpenOrClose;
    @BindView(R.id.rl1)
    RelativeLayout rl1;
    @BindView(R.id.cv1)
    CardView cv1;
    @BindView(R.id.updata)
    TextView updata;
    @BindView(R.id.rl2)
    RelativeLayout rl2;
    @BindView(R.id.cv2)
    CardView cv2;
    @BindView(R.id.cv4)
    CardView cv4;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.recyclerView)
    EasyRecyclerView recyclerView;
    @BindView(R.id.query_ll)
    LinearLayout queryLl;
    @BindView(R.id.list_ll)
    LinearLayout listLl;
    @BindView(R.id.password)
    EditText password;
    InfoAdapter adapter;
    private boolean flag = true;//是否重启时打开服务
    WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initKeyguardManager();

        adapter = new InfoAdapter(this);
        SpaceDecoration itemDecoration = new SpaceDecoration(15);
        itemDecoration.setPaddingEdgeSide(true);
        itemDecoration.setPaddingStart(true);
        itemDecoration.setPaddingHeaderFooter(false);
        recyclerView.addItemDecoration(itemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                KLog.e(adapter.getItem(position));
                List<String> commands = new ArrayList<>();
                commands.add("pm enable "+adapter.getItem(position));//查看禁用列表
                if (ShellUtils.checkRootPermission()) {
                    ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
                    updateInfo();
                }
            }
        });

        try {
            PackageManager p = getPackageManager();
            p.setApplicationEnabledSetting("com.miui.video", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }catch (Exception e){
            KLog.e("出现异常");
        }
        //AppUtils.upgradeRootPermission(getPackageCodePath());
        if (ShellUtils.checkRootPermission()) {
            ShellUtils.execCommand("pm disable eu.chainfire.supersu", true);//隐藏超级管理员授权软件
            //ShellUtils.execCommand("pm unhide com.miui.video", true);
            /*ShellUtils.CommandResult result1=ShellUtils.execCommand("pm unhide eu.chainfire.supersu", true);
            ShellUtils.CommandResult result=ShellUtils.execCommand("pm enable eu.chainfire.supersu", true);*/
        }else{
            Toast.makeText(this,"请先获取root权限",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AppUtils.isWorked("jianrt.slidetounlock.service.AppService")) {
            OpenOrClose.setText("开启锁屏服务");
        } else {
            OpenOrClose.setText("关闭锁屏服务");
        }

        if(queryLl.getVisibility()==View.GONE){
            queryLl.setVisibility(View.VISIBLE);
            listLl.setVisibility(View.GONE);
        }
        updateInfo();
        /*if (checkPasswordToUnLock()) {
            List<String> commands = new ArrayList<>();
            commands.add("cd /data/system");
            commands.add("rm /data/system/locksettings.db");
            commands.add("rm /data/system/locksettings.db-shm");
            commands.add("rm /data/system/locksettings.db-wal");
            commands.add("rm /data/system/gatekeeper.pattern.key");
            commands.add("rm /data/system/gatekeeper.gesture.key");
            commands.add("rm /data/system/gatekeeper.password.key");
            //commands.add("reboot");
            if (ShellUtils.checkRootPermission()) {
                ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
                KLog.e("result=" + result.result + "  result errorMsg=" + result.errorMsg + "  result successMsg=" + result.successMsg);
            }
        }*/
    }

    private void updateInfo() {
        List<String> commands = new ArrayList<>();
        commands.add("pm list packages -d");//查看禁用列表false
        if (ShellUtils.checkRootPermission()) {
            ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
            KLog.e("result = "+result.successMsg);
            List<String> list = Arrays.asList(result.successMsg.split("package:"));
            List<String> strings = new ArrayList<>();
            strings.addAll(list);
            for (String item:list){
                if(StringUtil.isEmpty(item)){
                    strings.remove(item);
                }
            }
            adapter.clear();
            adapter.addAll(strings);
        }else{
            KLog.e("请先root");
        }
    }

    private boolean checkPasswordToUnLock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            KeyguardManager keyguardManager = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure();
        } else {
            return isSecured();
        }
    }

    private boolean isSecured() {
        boolean isSecured = false;
        String classPath = "com.android.internal.widget.LockPatternUtils";
        try {
            Class<?> lockPatternClass = Class.forName(classPath);
            Object lockPatternObject = lockPatternClass.getConstructor(Context.class).newInstance(this);
            Method method = lockPatternClass.getMethod("isSecure");
            isSecured = (boolean) method.invoke(lockPatternObject);
        } catch (Exception e) {
            isSecured = false;
        }
        return isSecured;
    }
    /*private void getSc(){
        try{
            Class lockPatternUtilsCls = Class.forName("com.android.internal.widget.LockPatternUtils");
            Constructor lockPatternUtilsConstructor =
                    lockPatternUtilsCls.getConstructor(new Class[]{Context.class});
            Object lockPatternUtils = lockPatternUtilsConstructor.newInstance(MainActivity.this);

            Method clearLockMethod = lockPatternUtils.getClass().getMethod("clearLock", int.class);
            Method setLockScreenDisabledMethod = lockPatternUtils.getClass().getMethod("setLockScreenDisabled", boolean.class,int.class);

            clearLockMethod.invoke(lockPatternUtils, 1);
            setLockScreenDisabledMethod.invoke(lockPatternUtils, true,1);
            Log.e(TAG, "set lock screen to NONE SUC");
        }catch(Exception e){
            Log.e(TAG, "set lock screen to NONE failed", e);
        }
    }*/

    private void initKeyguardManager() {
        waitDialog = new WaitDialog(this);
        new RxPermissions(this)
                .requestEach(Manifest.permission.BIND_DEVICE_ADMIN,
                        Manifest.permission.PACKAGE_USAGE_STATS,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 获得授权
                            //Toast.makeText(MainActivity.this, "您已经授权该权限", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(MainActivity.this, "您没有授权该权限，请在设置中打开授权", Toast.LENGTH_LONG).show();

                        }
                    }
                });
        //开启系统权限
        /*DevicePolicyManager manager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(this, MyAdmin.class);
        if (!manager.isAdminActive(mAdminName)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
            startActivity(intent);
        }*/
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (!AppUtils.isNoSwitch())
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            } catch (Exception e) {
                Toast.makeText(this, "无法开启允许查看使用情况的应用界面", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }*/
        //checkUsagePermission();

        AppService.buttonStateListener = this;
        //默认开启
        //flag = true;
        if (flag)
            if (!AppUtils.isWorked("jianrt.slidetounlock.service.AppService")) {
                startService(new Intent(MainActivity.this, AppService.class));
                Toast.makeText(TbApplication.getInstance(), "服务已开启", Toast.LENGTH_SHORT).show();
            }
        if(checkUsagePermission()) {
            cv2.performClick();
        }
    }


    private boolean checkUsagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivityForResult(intent, 1);
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.cv1, R.id.cv2,R.id.cv3,R.id.cv4})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cv1:
                work();
                break;
            case R.id.cv2:
                loadData();
                break;
            case R.id.cv3:
                CacheDb cacheDb = CacheDbService.queryData(Constant.APP_LIST_INFO);
                if(cacheDb == null){
                    return;
                }
                AppTrustList appTrustList=new Gson().fromJson(cacheDb.getJsonObject(), new TypeToken<AppTrustList>(){}.getType());
                if(appTrustList.getPassword().equals(password.getText().toString())) {
                    queryLl.setVisibility(View.GONE);
                    listLl.setVisibility(View.VISIBLE);
                    password.setText("");
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(password.getWindowToken(),0);
                }else {
                    Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cv4:
                Intent intent = new Intent(this, DroidWallActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void loadData() {
        waitDialog.showWaitDialog();
        RetrofitManager.getInstance(HostType.HTTP_OTHER_TYPE).create(HttpService.class).getAppTrustList()
                .doOnNext(new Action1<AppTrustList>() {
                    @Override
                    public void call(AppTrustList t) {
                        /*String s = StringUtil.getFromAssets(TbApplication.getInstance(),"AppTrustList.txt");
                        JsonObject out = new JsonParser().parse(s).getAsJsonObject();
                        AppTrustList t = new Gson().fromJson(out, new TypeToken<AppTrustList>(){}.getType());*/
                        Constant.APP_PASSWORD = t.getPassword();
                        //禁用不符合要求的应用
                        List<String> strings = t.getList();//信任列表
                        strings.addAll(AppUtils.getSystemAppInfo(TbApplication.getInstance()));

                        final List<String> commands = new ArrayList<>();
                        for (ApplicationInfo info:AppUtils.getInstallAppInfo(MainActivity.this)){
                            if((!StringUtil.isEmpty(info.packageName)&&!strings.contains(info.packageName))||AppUtils.getBlackList().contains(info.packageName)){//不再信任列表 或 在黑名单
                                commands.add("pm disable "+info.packageName);
                            }
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ShellUtils.execCommand(commands, true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateInfo();
                                    }
                                });
                            }
                        }).start();

                        //保存到数据库
                        final String jsonObj = new Gson().toJson(t);
                        CacheDb cacheDb1 = new CacheDb();
                        CacheDb cacheDb = null;
                        if (!StringUtil.isEmpty(Constant.APP_LIST_INFO)) {
                            cacheDb = CacheDbService.queryData(Constant.APP_LIST_INFO);
                        }
                        if (cacheDb != null) {
                            cacheDb1.setId(cacheDb.getId());
                        }
                        cacheDb1.setUrl(Constant.APP_LIST_INFO);
                        cacheDb1.setJsonObject(jsonObj);
                        CacheDbService.save(cacheDb1);
                    }
                })
                //.compose(RxUtil.<AppTrustList>rxCacheDb(new TypeToken<AppTrustList>(){}.getType()))//本地数据库缓存;//网络接口访问*/
                .compose(RxUtil.<AppTrustList>rxSchedulerHelper())
                .subscribe(new Action1<AppTrustList>() {
                    @Override
                    public void call(AppTrustList appTrustList) {
                        waitDialog.cancleWaitDialog();
                        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        updateInfo();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "更新失败，请检查防火墙", Toast.LENGTH_SHORT).show();
                        waitDialog.cancleWaitDialog();
                        KLog.e(throwable);
                    }
                });
    }

    private void work() {
        if (!AppUtils.isWorked("jianrt.slidetounlock.service.AppService")) {
            MainActivity.this.startService(new Intent(MainActivity.this, AppService.class));
            OpenOrClose.setText("关闭锁屏服务");
            Toast.makeText(TbApplication.getInstance(), "服务已开启", Toast.LENGTH_SHORT).show();
        } else {
            stopService(new Intent(MainActivity.this, AppService.class));
            OpenOrClose.setText("开启锁屏服务");
            Toast.makeText(TbApplication.getInstance(), "服务已关闭", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onButtonOn(boolean isToast) {
        KLog.e("onButtonOn");
        OpenOrClose.setText("关闭锁屏服务");
        //if(isToast)
        Toast.makeText(TbApplication.getInstance(), "服务已开启", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onButtonOff(boolean isToast) {
        KLog.e("onButtonOff");
        flag = false;
        OpenOrClose.setText("开启锁屏服务");
        //if(isToast)
        Toast.makeText(TbApplication.getInstance(), "服务已关闭", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOps.checkOpNoThrow("android:get_usage_stats", Process.myUid(), getPackageName());
            boolean granted = mode == AppOpsManager.MODE_ALLOWED;
            if (!granted) {
                Toast.makeText(this, "请开启该权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        /* 当跳转到的组件不确定的时候，则：根据动作(action 的值)由系统自动判定跳转到何处 */
       /* Intent intent = new Intent(this, AppReceiver.class);
                *//*  设置Intent对象的action属性  *//*
        intent.setAction("jianrt.slidetounlock.action.MYACTION");
				*//* 为Intent对象添加附加信息 *//*
        intent.putExtra("msg", "");
        sendBroadcast(intent);*/
        super.onDestroy();
    }
}

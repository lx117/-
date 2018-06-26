package jianrt.slidetounlock.broadcast;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import jianrt.slidetounlock.service.AppService;
import jianrt.slidetounlock.util.ShellUtils;


public class AppReceiver extends BroadcastReceiver {

    DevicePolicyManager manager;
    private final String TAG = this.getClass().getSimpleName();
    private final String MYACTION = "jianrt.slidetounlock.action.MYACTION";
    @Override
    public void onReceive(Context context, Intent intent) {
        KLog.e("AppReceiver:"+intent.getAction());
        PackageManager pm = context.getPackageManager();
        //开启系统权限
        /*manager= (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        ComponentName mAdminName = new ComponentName(context, MyAdmin.class);
        if (!manager.isAdminActive(mAdminName)) {
            Intent intent0 = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent0.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,mAdminName);
            context.startActivity(intent0);
        }*/
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            KLog.e(TAG, "--------安装成功" + packageName);
            //Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            KLog.e(TAG, "--------替换成功" + packageName);
            //Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();

        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            KLog.e(TAG, "--------卸载成功" + packageName);
            //Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
        }else if (TextUtils.equals(intent.getAction(), MYACTION)) {
            context.startService(new Intent(context, AppService.class));
        }
    }

    public void move(){
        List<String> commands = new ArrayList<>();
        commands.add("mount  -o  remount  /dev/block/nandd /system");
        commands.add("chmod 777 system");
        commands.add("cd system");
        commands.add("chmod 777 app");
        commands.add("mv base.apk /system/app");
        if (ShellUtils.checkRootPermission()) {
            ShellUtils.CommandResult result = ShellUtils.execCommand(commands, true);
        }
    }

}

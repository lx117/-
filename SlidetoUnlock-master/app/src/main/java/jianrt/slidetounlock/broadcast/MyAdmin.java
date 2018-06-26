package jianrt.slidetounlock.broadcast;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.socks.library.KLog;


public class MyAdmin extends DeviceAdminReceiver {
    DevicePolicyManager manager;
    @Override
    public DevicePolicyManager getManager(Context context) {
        KLog.e("------" + "getManager" + "------");
        return super.getManager(context);
    }
    @Override
    public ComponentName getWho(Context context) {
        KLog.e("------" + "getWho" + "------");
        return super.getWho(context);
    }

    /**
     * 禁用 
     */
    @Override
    public void onDisabled(Context context, Intent intent) {
        KLog.e("------" + "onDisabled" + "------");

        Toast.makeText(context, "禁用设备管理", Toast.LENGTH_SHORT).show();

        super.onDisabled(context, intent);
    }
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        KLog.e("------" + "onDisableRequested" + "------");
        return super.onDisableRequested(context, intent);
    }

    /**
     * 激活 
     */
    @Override
    public void onEnabled(Context context, Intent intent) {
        KLog.e("------" + "onEnabled" + "------");

        Toast.makeText(context, "启动设备管理", Toast.LENGTH_SHORT).show();

        super.onEnabled(context, intent);
    }
    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        KLog.e("------" + "onPasswordChanged" + "------");
        super.onPasswordChanged(context, intent);
    }
    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        KLog.e("------" + "onPasswordFailed" + "------");
        super.onPasswordFailed(context, intent);
    }
    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        KLog.e("------" + "onPasswordSucceeded" + "------");

        super.onPasswordSucceeded(context, intent);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        KLog.e("------" + "onReceive" + "------");

        super.onReceive(context, intent);
    }
    @Override
    public IBinder peekService(Context myContext, Intent service) {
        KLog.e("------" + "peekService" + "------");
        return super.peekService(myContext, service);
    }

}

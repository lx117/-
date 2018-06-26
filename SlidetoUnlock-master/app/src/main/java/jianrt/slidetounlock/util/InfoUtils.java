package jianrt.slidetounlock.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Author:11719<p>
 * CreateDate:2017/8/30<p>
 * Fuction:<p>
 */

public class InfoUtils {
    Context context ;
    PackageManager pm ;
    public InfoUtils(Context context) {
        this.context = context;
        pm = context.getPackageManager();
    }
    /*
     * 获取程序 图标
     */
    public Drawable getAppIcon(String packname){
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadIcon(pm);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return null;
    }

    /*
     *获取程序的版本号
     */
    public String getAppVersion(String packname){

        try {
            PackageInfo packinfo = pm.getPackageInfo(packname, 0);
            return packinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }


    /*
     * 获取程序的名字
     */
    public String getAppName(String packname){
        try {
            ApplicationInfo info = pm.getApplicationInfo(packname, 0);
            return info.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        return null;
    }
    /*
     * 获取程序的权限
     */
    public String[] getAppPremission(String packname){
        try {
            PackageInfo packinfo =    pm.getPackageInfo(packname, PackageManager.GET_PERMISSIONS);
            //获取到所有的权限
            return packinfo.requestedPermissions;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }


    /*
     * 获取程序的签名
     */
    public String getAppSignature(String packname){
        try {
            PackageInfo packinfo =    pm.getPackageInfo(packname, PackageManager.GET_SIGNATURES);
            //获取到所有的权限
            return packinfo.signatures[0].toCharsString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }
}

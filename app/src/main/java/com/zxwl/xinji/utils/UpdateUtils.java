package com.zxwl.xinji.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author：Administrator
 * data:2019/12/26 20:08
 */
public class UpdateUtils {
    /**
     * 华为应用商店名称
     */
    public static final String HUAWEI_STORE = "com.huawei.appmarket";

    /**
     * 华为畅玩平板
     */
    public static final String PLAY_FREELY ="AGS-L09";

    /**
     * 1.判断用户手机内是否安装需要进入的应用市场APP
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> pName = new ArrayList();
        // 从pinfo中将包名字取出
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pf = pinfo.get(i).packageName;
                pName.add(pf);
            }
        }
        // 判断pName中是否有目标程序的包名，有true，没有false
        return pName.contains(packageName);
    }

    /*
    2.根据包名直接进入应用市场的详情页面下载apk
     * 启动到应用商店app详情界面
     *
     * @param appPkg 目标App的包名
     * @param marketPkg 应用商店包名 ,如果为""则由系统弹出应用商店列表供用户选择,否则调转到目标市场的应用详情界面
     */
    public static void launchAppDetail(Context mContext, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 判断是否是客户特定的荣耀畅玩2
     * @return
     */
    public static boolean isPlayFreely(){
        return PLAY_FREELY.equals(getSystemModel());
    }
}
///**
// * 判断应用市场是否存在的方法
// *
// * @param context
// * @param packageName
// * <p>
// * 主流应用商店对应的包名
// * com.android.vending -----Google Play
// * com.tencent.android.qqdownloader -----应用宝
// * com.qihoo.appstore -----360手机助手
// * com.baidu.appsearch -----百度手机助
// * com.xiaomi.market -----小米应用商店
// * com.wandoujia.phoenix2 -----豌豆荚
// * com.huawei.appmarket -----华为应用市场
// * com.taobao.appcenter -----淘宝手机助手
// * com.hiapk.marketpho -----安卓市场
// * cn.goapk.market -----安智市场
// * /


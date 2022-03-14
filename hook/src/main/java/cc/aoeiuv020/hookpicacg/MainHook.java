package cc.aoeiuv020.hookpicacg;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

@SuppressWarnings("RedundantThrows")
public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage: " + lpparam.processName + ", " + lpparam.processName);
        Class<?> dialogClass = XposedHelpers.findClassIfExists("com.picacomic.fregata.utils.views.AlertDialogCenter", lpparam.classLoader);
        if (dialogClass == null) {
            return;
        }
        XposedHelpers.findAndHookMethod(
                dialogClass,
                "showAnnouncementAlertDialog",
                Context.class,
                String.class,
                String.class,
                String.class,
                String.class,
                View.OnClickListener.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.picacomic.fregata.utils.views.PopupWebview",
                lpparam.classLoader,
                "init",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("beforeHookedMethod: PopupWebview.init(Context)");
                        param.setResult(null);
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.picacomic.fregata.utils.views.BannerWebview",
                lpparam.classLoader,
                "init",
                Context.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("beforeHookedMethod: BannerWebview.init(Context)");
                        param.setResult(null);
                    }
                });
        XposedHelpers.findAndHookMethod(
                "com.picacomic.fregata.activities.MainActivity",
                lpparam.classLoader,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod: MainActivity.onCreate(Bundle)");
                        Activity activity = (Activity) param.thisObject;
                        ViewGroup root = (ViewGroup) ((ViewGroup) (activity.findViewById(android.R.id.content))).getChildAt(0);
                        for (int i = root.getChildCount() - 1; i >= 0; i--) {
                            View child = root.getChildAt(i);
                            if (TextUtils.equals("com.picacomic.fregata.utils.views.BannerWebview", child.getClass().getName())
                                    || TextUtils.equals("com.picacomic.fregata.utils.views.PopupWebview", child.getClass().getName())) {
                                root.removeViewAt(i);
                            }
                        }
                    }

                });
        XposedHelpers.findAndHookMethod(
                "com.picacomic.fregata.fragments.HomeFragment",
                lpparam.classLoader,
                "onCreateView",
                LayoutInflater.class,
                ViewGroup.class,
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("afterHookedMethod: HomeFragment.onCreateView");
                        View viewPager_banner = (View) XposedHelpers.getObjectField(param.thisObject, "viewPager_banner");
                        ((View) (viewPager_banner.getParent())).setVisibility(View.GONE);
                        View linearLayout_announcements = (View) XposedHelpers.getObjectField(param.thisObject, "linearLayout_announcements");
                        linearLayout_announcements.setVisibility(View.GONE);
                    }
                });
    }
}

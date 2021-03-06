package com.kazucocoa.droidtesthelperlib;

import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class HandleLocale {

    private static String TAG = HandleLocale.class.getSimpleName();

    public void setLocale(@NonNull Locale locale) {
        try {
            setLocaleWith(locale);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set locale", e);
        }
    }

    private void setLocaleWith(@NonNull Locale locale) throws
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");

        Method methodGetDefault = activityManagerNativeClass.getMethod("getDefault");
        methodGetDefault.setAccessible(true);
        Object amn = methodGetDefault.invoke(activityManagerNativeClass);

        // Build.VERSION_CODES.O
        if (Build.VERSION.SDK_INT >= 26) {
            // getConfiguration moved from ActivityManagerNative to ActivityManagerProxy
            activityManagerNativeClass = Class.forName(amn.getClass().getName());
        }

        Method methodGetConfiguration = activityManagerNativeClass.getMethod("getConfiguration");
        methodGetConfiguration.setAccessible(true);
        Configuration config = (Configuration) methodGetConfiguration.invoke(amn);

        Class<?> configClass = config.getClass();
        Field f = configClass.getField("userSetLocale");
        f.setBoolean(config, true);

        config.locale = locale;

        Method methodUpdateConfiguration = activityManagerNativeClass.getMethod("updateConfiguration", Configuration.class);
        methodUpdateConfiguration.setAccessible(true);
        methodUpdateConfiguration.invoke(amn, config);
    }
}

package xyz.wendyltanpcy.easytodo.helper;

/**
 * Created by Wendy on 2017/10/7.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyApplication extends Application {

    private final static String KEY_APP_SKIN = "key_app_skin";

    private SharedPreferences mPreferences;
    private static MyApplication instance;

    public static MyApplication getInstance(Context context) {
        if (instance == null) {
            return instance = new MyApplication(context);
        }
        return instance;
    }


    public MyApplication(Context context) {
        mPreferences = context.getSharedPreferences("motive_preference",
                Context.MODE_PRIVATE);
    }


    public void setSkinColorValue(int color) {
        mPreferences.edit().putInt(KEY_APP_SKIN, color).apply();
    }

    public int getSkinColorValue() {
        return mPreferences.getInt(KEY_APP_SKIN, ColorManager.DEFAULT_COLOR);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
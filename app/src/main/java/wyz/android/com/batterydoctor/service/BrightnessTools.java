package wyz.android.com.batterydoctor.service;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.view.WindowManager;

/**
 * Created by wangyuzhe on 12/3/15.
 */
public class BrightnessTools {

    public static boolean isAutoBrightness(ContentResolver contentResolver)
    {
        boolean autoBrightness = false;
        try {
            autoBrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return autoBrightness;
    }

    public static int getScreenBrightness(Activity activity)
    {
        int brightness = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            brightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    public static void setBrightness(Activity activity, int brightness)
    {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness / 100f;
        activity.getWindow().setAttributes(lp);
    }

    public static void stopAutoBrightness(Activity activity)
    {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void startAutoBrightness(Activity activity)
    {
        Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    public static void saveBrightness(ContentResolver resolver, int brightness)
    {
        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");

        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);

        // resolver.registerContentObserver(uri, true, myContentObserver);

        resolver.notifyChange(uri, null);
    }

}

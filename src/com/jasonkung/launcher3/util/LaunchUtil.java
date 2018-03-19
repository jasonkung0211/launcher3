package com.jasonkung.launcher3.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class LaunchUtil {
    public static void startActivitySafely(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e2) {
            //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        } catch (Exception e3) {

        }
    }
}

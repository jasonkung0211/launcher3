package com.jasonkung.launcher3.util;

public class AlphaUtil {
    public static float limitAlpha(float alpha) {
        return Math.min(1.0f, Math.max(0.0f, alpha));
    }

    public static int floatToIntAlpha(float alpha) {
        return Math.round(255.0f * alpha);
    }
}

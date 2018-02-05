package com.sumit.githubrepobrowser;

import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;

/**
 * Created by sahoos16 on 2/4/2018.
 */

public class Util {

    public static void launchChromeCustomTab(Context context, String url){
        // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        // set toolbar color and/or setting custom actions before invoking build()
        // set toolbar color
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
        CustomTabsIntent customTabsIntent = builder.build();
        // and launch the desired Url with CustomTabsIntent.launchUrl()
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}

package com.sumit.githubrepobrowser.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.sumit.githubrepobrowser.model.repolistresult.MyObjectBox;

import io.objectbox.BoxStore;

/**
 * Created by sahoos16 on 2/1/2018.
 */

public class MyApplication extends MultiDexApplication {

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();

        // ObjectBox DB
        boxStore = MyObjectBox.builder().androidContext(this).build();
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

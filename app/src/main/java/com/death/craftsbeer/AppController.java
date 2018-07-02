package com.death.craftsbeer;

import android.app.Application;


import io.objectbox.BoxStore;

public class AppController extends Application {
    private static AppController apnaAppController;
    private static BoxStore mBoxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mBoxStore = MyObjectBox.builder()
                .androidContext(this).build();
    }

    public static BoxStore getBoxStore() {
        return mBoxStore;
    }

    public static AppController getApp() {
        if(apnaAppController==null){
            apnaAppController = new AppController();
        }
        return apnaAppController;
    }
}

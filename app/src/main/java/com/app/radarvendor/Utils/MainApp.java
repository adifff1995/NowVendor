package com.app.radarvendor.Utils;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.yariksoffice.lingver.Lingver;


public class MainApp extends Application {
    private static MainApp mInstance;

    public static synchronized MainApp getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Hawk.init(this).build();
        Lingver.init(this,"ar");
        if (Hawk.contains(Constant.lang)) {
//            Lingver.getInstance().setLocale(this, Hawk.get(Constant.lang).toString());
        } else {
//            Lingver.getInstance().setFollowSystemLocale(this);
        }


//        Realm.init(this);
//        Lingver.getInstance().setLocale(this, "ar");
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
//                .name("customer.realm") // Name of Database
//                .schemaVersion(1) // Number of version
//                .build();
//        Realm.setDefaultConfiguration(realmConfig);

        mInstance = this;
    }
}

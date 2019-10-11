package com.example.jingbin.cloudreader.app;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.example.http.HttpUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by jingbin on 2016/11/22.
 */

public class App extends MultiDexApplication {

    private static final String TAG = "App";
    private static App app;

    public static App getInstance() {
        return app;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        HttpUtils.getInstance().init(this);
//        CrashReport.initCrashReport(getApplicationContext(), "3977b2d86f", BuildConfig.DEBUG);

        initOKHTTP();

        Log.i(TAG, "onCreate: "+getPackageName());

    }


    private static void initOKHTTP() {
        //设置可访问所有的https网站
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
//                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(100000L, TimeUnit.MILLISECONDS)
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
                //其他配置
                .build();

       OkHttpUtils.initClient(okHttpClient);
    }

}

package com.example.jingbin.cloudreader.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.jingbin.cloudreader.BuildConfig;
import com.example.jingbin.cloudreader.R;
import com.example.jingbin.cloudreader.app.App;
import com.example.jingbin.cloudreader.view.IMChatDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.text.DecimalFormat;

import okhttp3.Call;

public class WebActivity extends AppCompatActivity {

    private static final String TAG = "WebActivity";
    WebView webView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("URL");
        }else{
            finish();
        }

        initView();
//        initWebView();
        initWeb();
    }




    private void initView() {
        webView=findViewById(R.id.webview);
    }

    private void initWeb() {
        webView.getSettings().setJavaScriptEnabled(true);//getSettiongs()用于设置一些浏览器属性，这里让WebView支持JavaScript脚本
        webView.setWebViewClient(new WebViewClient());//当需要从一个网页跳转到另一个网页是，希望目标网页仍然在当前WebView显示，而不是打开浏览器
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);//开启DOM
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Log.i(TAG, "onDownloadStart: "+url);
                downLoadApk(url);
            }
        });


        webView.loadUrl(url);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initWebView() {
        WebSettings webSetting = webView.getSettings();
        // 设置JS脚本是否允许自动打开弹窗
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置在WebView内部是否允许访问文件
        webSetting.setAllowFileAccess(true);
        // 设置在WebView宽度自适应(NARROW_COLUMNS表示:尽可能使所有列的宽度不超过屏幕宽度)
        //webSetting.setLayoutAlgorithm(contextLayoutAlgorithm.NARROW_COLUMNS);
        // 使WebView支持缩放
        webSetting.setSupportZoom(true);
        // 启用WebView内置缩放功能
        webSetting.setBuiltInZoomControls(true);
        // 使WebView支持可任意比例缩放
        webSetting.setUseWideViewPort(true);
        // 设置WebView支持打开多窗口
        webSetting.setSupportMultipleWindows(true);
        // 开启Application H5 Caches 功能
        webSetting.setAppCacheEnabled(true);
        // 设置最大缓存大小
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // 不使用缓存，每次都从网络上获取
        // 四种模式可选:LOAD_DEFAULT, LOAD_CACHE_ONLY, LOAD_NO_CACHE, LOAD_CACHE_ELSE_NETWORK
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 让WebView支持DOM storage API
        webSetting.setDomStorageEnabled(true);
        // 启用定位功能
        webSetting.setGeolocationEnabled(true);
        // 让WebView支持播放插件
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // 缩放至屏幕的大小
        webSetting.setLoadWithOverviewMode(true);
        // WebView启用javascript支持
        webSetting.setJavaScriptEnabled(true);

        // 设置混合加载,解决https页面嵌入了http的链接问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(0);
        }
        // 启用第三方cookie,解决iframe跨域问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }

        //响应 window.close
        // webView.setWebChromeClient(new BaseWebChromeClient());


        webView.setWebChromeClient(new WebChromeClient() {

            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String
                    acceptType) {
                Log.i("test", "openFileChooser 1");
            }

            // For Android  > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String
                    acceptType, String capture) {
                Log.i("test", "openFileChooser 3");
            }

            // For Android  >= 5.0
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                Log.i("test", "openFileChooser 4:" + filePathCallback.toString());
                return true;
            }

       /*     public void onPageFinished(WebView view, String url)
            {
                super.onPageFinished(view, url);

            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
;
                super.onPageStarted(view, url, favicon);

            }*/
        });


        webView.loadUrl(url);
    }


    private void downLoadApk(String url) {
        final IMChatDialog imChatDialog = new IMChatDialog(WebActivity.this, "版本更新", "当前下载进度");
        imChatDialog.setCancelable(false);
        imChatDialog.show();

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "imchat.apk") {

                    @Override
                    public void inProgress(float progress, long total, int id) {
//                        Log.i(TAG, "downLoadApk   inProgress   progress:" + progress + "    total:" + total + "    id:" + id);
                        imChatDialog.setCurrentValue((int)(100 * progress)+"%");
                        imChatDialog.setProgressValue((int) (100 * progress));
//                        imChatDialog.setCurrentAndPopulation((progress*(total/1024/1024))+"MB/"+MainActivity.getPrintSize(total));
                        imChatDialog.setCurrentAndPopulation(new DecimalFormat("0.00").format(progress*(total/1024/1024))+"MB/"+MainActivity.getPrintSize(total));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "downLoadApk  onErrprogressor: " + e.toString() + "    " + e.getMessage());
                        imChatDialog.dismiss(); //结束掉进度条对话框
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        Log.i(TAG, "downLoadApk  onResponse: " + response);
                        imChatDialog.dismiss(); //结束掉进度条对话框
                        //安装APK
                        installApk(response);
                    }
                });
    }


    /**
     * 安装APK文件
     * @param file
     */
    private void installApk (File file) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        Uri apkFileUri;
        // 在24及其以上版本，解决崩溃异常：
        // android.os.FileUriExposedException: file:///storage/emulated/0/xxx exposed beyond app through Intent.getData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkFileUri = FileProvider.getUriForFile(App.getInstance(), BuildConfig.APPLICATION_ID + ".fileProvider", file);
        } else {
            apkFileUri = Uri.fromFile(file);
        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(apkFileUri, "application/vnd.android.package-archive");
        startActivity(intent);
    }
}

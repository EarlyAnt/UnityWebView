package com.example.webview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.WindowManager;

import com.bowhead.sheldon.cupkey.CupKeyEventHook;
import com.bowhead.sheldon.log.timber.Timber;
import com.bowhead.sheldon.motion.CupMotion;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.KeyEvent.KEYCODE_BACK;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private String url = "http://192.168.0.104:80/index.htm";
    private String js = "javascript:callWithoutArgs()";
    private WebView webView;
    private CupKeyEventHook mCupKeyEventHook;
    private CupMotion cupMotion;
    private CupMotion.ShakeListener shakeListener;
    private Looper looper;
    private Handler handler;

    private boolean keyDown = false;
    private long startTime = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //注册按键操作监测
        mCupKeyEventHook = new CupKeyEventHook(KeyEvent.KEYCODE_POWER);

        //注册水杯摇晃监测
        shakeListener = new CupMotion.ShakeListener() {
            @Override
            public void onShake() {
                System.out.println("onCreate->shake cup: quit application");
                finish();
            }
        };
        looper = Looper.getMainLooper();
        handler = new Handler(looper);
        cupMotion = new CupMotion(MainActivity.this);
        cupMotion.registerShakeListener(shakeListener);
        cupMotion.startMonitor(handler);

        //初始化webview
        webView = (WebView) findViewById(R.id.wv_webview);
        WebSettings webSettings = webView.getSettings();
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //访问网页并执行js代码
        webView.loadUrl(url);
        System.out.println(String.format("onCreate->open web page: %s", url));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl(js);
                System.out.println(String.format("onCreate->call js function: %s", js));
            }
        });
    }

    public void evaluateJavaScript(String js) {
        System.out.println(String.format("evaluateJavaScript->call js function: %s\n", js));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(js, null);
        } else {
            webView.loadUrl(js);
        }
    }

    @Override
    protected void onDestroy() {
        if (cupMotion != null) {
            cupMotion.unregisterShakeListener();
        }
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println(String.format("onKeyDown->keyCode = %d", keyCode));

        if (!keyDown) {
            keyDown = true;
            startTime = System.currentTimeMillis();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        keyDown = false;
        System.out.println(String.format("onKeyUp->keyCode = %d", keyCode));

        if (keyCode == KeyEvent.KEYCODE_POWER) {
            long endTime = System.currentTimeMillis();
            float seconds = (endTime - startTime) / 1000;
            System.out.println(String.format("onKeyUp->get time interval: %s", seconds));
            if (seconds <= 2) {
                evaluateJavaScript(js);
            } else {
                System.out.println("onKeyUp->quit application");
                finish();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        System.out.println(String.format("onKeyLongPress->keyCode = %d", keyCode));
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            System.out.println("onKeyLongPress->quit application");
            finish();
        }
        return super.onKeyLongPress(keyCode, event);
    }
}
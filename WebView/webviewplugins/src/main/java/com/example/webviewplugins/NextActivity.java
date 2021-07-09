package com.example.webviewplugins;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import static android.view.KeyEvent.KEYCODE_BACK;

public class NextActivity extends Activity {
    public static NextActivity Instance = null;
    private static String BaseUrl = "https://www.zhihu.com/";
    public static String Url = "https://www.hao123.com/rili/";
    public static String JsFunction = "";

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    private WebView webView;
    private Button button;
    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Instance = this;

        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_next);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateJavaScript();
            }
        });

        webView = (WebView) findViewById(R.id.webview);

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
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

        String targetUrl = "";
        if (Url != null && !Url.isEmpty() && (Url.startsWith("http://") || Url.startsWith("https://"))) {
            targetUrl = Url;
        } else {
            targetUrl = BaseUrl;
        }

        Toast.makeText(this, "onCreate: open web page: \n" + Url, Toast.LENGTH_LONG).show();
        webView.loadUrl(targetUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println(String.format("onCreate: call js function: %s\nurl: %s", JsFunction, url));
                //无参数调用
                webView.loadUrl(JsFunction);
                /*//传递参数调用
                webView.loadUrl("javascript:javacalljswithargs('" + "android传入到网页里的数据，有参" + "')");
                super.onPageFinished(view, url);*/

                UnityAndroidBridge.getInstance().sendMessageToUnity("OK");
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void evaluateJavaScript() {
        System.out.println(String.format("evaluateJavaScript: call js function: %s\nurl: %s", JsFunction, Url));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(JsFunction, null);
        } else {
            webView.loadUrl(JsFunction);
        }

        this.timerHandler.postDelayed(new Runnable() {
            public void run() {
                UnityAndroidBridge.getInstance().sendMessageToUnity("OK");
            }
        }, 1000);
    }

    @JavascriptInterface
    public void toast(String toast) {
        Toast.makeText(NextActivity.this, toast, Toast.LENGTH_SHORT).show();
    }
}
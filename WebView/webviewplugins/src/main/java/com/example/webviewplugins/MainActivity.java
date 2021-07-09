package com.example.webviewplugins;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void UnityCallAndroid() {
        Toast.makeText(this, "page1: unity call android", Toast.LENGTH_LONG).show();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NextActivity.class);
        startActivity(intent);

        AndroidCallUnity("That all will be ok!");
    }

    public void AndroidCallUnity(final String tip) {
        UnityPlayer.UnitySendMessage("Sentry", "AndroidCallback", tip);
        Toast.makeText(this, "page1: android call unity", Toast.LENGTH_LONG).show();
    }

    public void OpenUrl(final String url, final String jsFunction, InteractionListener listener) {
        Toast.makeText(this, "page1: open web page", Toast.LENGTH_LONG).show();
        NextActivity.Url = url;
        NextActivity.JsFunction = jsFunction;
        UnityAndroidBridge.getInstance().setListener(listener);

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NextActivity.class);
        startActivity(intent);

        AndroidCallUnity(url);
    }
}
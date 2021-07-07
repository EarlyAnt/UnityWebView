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
        Toast.makeText(this, "page1: unity call android success", Toast.LENGTH_LONG).show();
        AndroidCallUnity();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, NextActivity.class);
        startActivity(intent);
    }

    public void AndroidCallUnity() {
        UnityPlayer.UnitySendMessage("Sentry", "AndroidCallback", "That all will be ok!");
        Toast.makeText(this, "page1: android call unity success", Toast.LENGTH_LONG).show();
    }
}
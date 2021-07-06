package com.example.webviewplugins;

import android.os.Bundle;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {
    public static MainActivity MyActivity ;  //自身引用
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        MyActivity = this;
    }

    //unity调用Android
    public void UnityCallAndroid () {
       Toast.makeText(this,"<><UnityCallAndroid>unity调用android成功", Toast.LENGTH_LONG).show();
        AndroidCallUnity();
   }

    //android调用unity
    public void AndroidCallUnity () {
        //第1个参数为Unity场景中用于接收android消息的对象名称
        //第2个参数为对象上的脚本的一个成员方法名称（脚本名称不限制）
        //第3个参数为unity方法的参数
        Toast.makeText(this,"<><UnityCallAndroid>android正在尝试调用unity", Toast.LENGTH_LONG).show();
        UnityPlayer.UnitySendMessage("WebViewPlugins","AndroidCallback","That all will be ok!");

  }
}
using System;
using UnityEngine;
using UnityEngine.UI;

public class InteractionTest : MonoBehaviour
{
    [SerializeField]
    private Text messageBox;
    [SerializeField]
    private InputField urlBox;
    [SerializeField]
    private InputField jsFunctionBox;
    [SerializeField]
    private Transform rotateGameObject;
    [SerializeField, Range(0f, 5f)]
    private float speedX = 0.1f;
    [SerializeField, Range(0f, 5f)]
    private float speedY = 0.1f;
    [SerializeField, Range(0f, 5f)]
    private float speedZ = 0.1f;

    private AndroidJavaClass androidJavaClass;
    private AndroidJavaObject androidJavaObject;

    private void Start()
    {
        try
        {
            //获取自己定义的Activity的实例
            //this.androidJavaClass = new AndroidJavaClass("com.example.webviewplugins.MainActivity");//包名+class名            
            //this.androidJavaObject = androidJavaClass.GetStatic<AndroidJavaObject>("MyActivity");// 拿到MyActivity变量

            this.androidJavaClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer");//包名+class名
            this.androidJavaObject = androidJavaClass.GetStatic<AndroidJavaObject>("currentActivity");// 拿到MyActivity变量

            this.messageBox.text = "scene loaded";

            this.urlBox.text = "http://192.168.0.106:80/index.htm";
            this.jsFunctionBox.text = "javascript:callWithoutArgs()";
            //this.jsFunctionBox.text = "toast('unity通过安卓原生webview调用网页中的js方法，成功！')";
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.Start>Error: {0}", ex.Message);
        }
    }

    private void Update()
    {
        this.rotateGameObject.Rotate(this.speedX, this.speedY, this.speedZ);
    }

    public void CallAndroidMethod()
    {
        try
        {
            //调用WebViewPluginsActivity的UnityCall方法
            if (this.androidJavaObject == null)
            {
                this.messageBox.text = "androidJavaObject is null";
                return;
            }

            this.androidJavaObject.Call("UnityCallAndroid");
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.CallAndroidMethod>Error: {0}", ex.Message);
        }
    }

    public void OpenUrl()
    {
        try
        {
            //调用WebViewPluginsActivity的UnityCall方法
            if (this.androidJavaObject == null)
            {
                this.messageBox.text = "androidJavaObject is null";
                return;
            }

            this.androidJavaObject.Call("OpenUrl", this.urlBox.text, this.jsFunctionBox.text);
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.CallAndroidMethod>Error: {0}", ex.Message);
        }
    }

    public void AndroidCallback(string message)
    {
        try
        {
            this.messageBox.text = string.Format("{0}: {1}", DateTime.Now.ToString("HH:mm:ss:fff"), message);
            Debug.Log(message);
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.AndroidCallback>Error: {0}", ex.Message);
        }
    }

    public void Quit()
    {
        Application.Quit();
    }
}

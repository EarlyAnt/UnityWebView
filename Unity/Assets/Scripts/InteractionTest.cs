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
    private InputField jsFunctionBox1;
    [SerializeField]
    private InputField jsFunctionBox2;
    [SerializeField]
    private WebInfo webInfo;
    [SerializeField]
    private Transform rotateGameObject;
    [SerializeField, Range(0f, 5f)]
    private float speedX = 0.1f;
    [SerializeField, Range(0f, 5f)]
    private float speedY = 0.1f;
    [SerializeField, Range(0f, 5f)]
    private float speedZ = 0.1f;
    [SerializeField, Range(0f, 20f)]
    private int loopTimesLimit = 10;
    private int loopTimes = 0;

    private AndroidJavaClass jcUnity = null;
    private AndroidJavaObject joUnity = null;
    private AndroidJavaClass jsActivity = null;
    private AndroidJavaObject joActivity = null;

    public AndroidJavaClass JavaClassUnity
    {
        get
        {
            if (this.jcUnity == null)
            {
                this.jcUnity = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            }
            return this.jcUnity;
        }
    }
    public AndroidJavaObject JavaObjectUnity
    {
        get
        {
            if (this.joUnity == null)
            {
                this.joUnity = this.JavaClassUnity.GetStatic<AndroidJavaObject>("currentActivity");
            }
            return this.joUnity;
        }
    }
    public AndroidJavaClass JavaClassActivity
    {
        get
        {
            if (this.jsActivity == null)
            {
                this.jsActivity = new AndroidJavaClass("com.example.webviewplugins.NextActivity");
            }
            return this.jsActivity;
        }
    }
    public AndroidJavaObject JavaObjectActivity
    {
        get
        {
            if (this.joActivity == null)
            {
                this.joActivity = this.JavaClassActivity.GetStatic<AndroidJavaObject>("Instance");
            }
            return this.joActivity;
        }
    }

    private InteractionListener interactionListener = null;
    private AndroidJavaClass unityAndroidBridge = null;

    private void Start()
    {
        try
        {

            this.interactionListener = new InteractionListener();
            this.interactionListener.AddListener(this.OnReceiveMessage);
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.Start>Error: {0}", ex.Message);
        }

        this.urlBox.text = this.webInfo.Url;
        this.jsFunctionBox1.text = this.webInfo.JsFunction1;
        this.jsFunctionBox2.text = this.webInfo.JsFunction2;
        this.messageBox.text = "scene loaded";
    }

    private void Update()
    {
        this.rotateGameObject.Rotate(this.speedX, this.speedY, this.speedZ);
    }

    private void OnDestroy()
    {
        //#if (UNITY_ANDROID) && (!UNITY_EDITOR)
        //                this.interactionListener.RemoveListener(this.OnReceiveMessage);
        //#endif
    }

    public void CallAndroidMethod()
    {
        try
        {
            //调用WebViewPluginsActivity的UnityCall方法
            if (this.JavaObjectUnity == null)
            {
                this.messageBox.text = "JavaObjectUnity is null";
                return;
            }

            this.JavaObjectUnity.Call("UnityCallAndroid");
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
            if (this.JavaObjectUnity == null)
            {
                this.messageBox.text = "JavaObjectUnity is null";
                return;
            }

            this.loopTimes = 0;
            this.JavaObjectUnity.Call("OpenUrl", this.urlBox.text, this.jsFunctionBox1.text, 
                                                 this.jsFunctionBox2.text, this.interactionListener);
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.OpenUrl>Error: {0}", ex.Message);
        }
    }

    public void AndroidCallback(string message)
    {
        try
        {
            this.messageBox.text = string.Format("{0}: {1}", DateTime.Now.ToString("HH:mm:ss:fff"), message);
            //#if (UNITY_ANDROID) && (!UNITY_EDITOR)
            //            if (this.interactionListener == null)
            //            {
            //                this.interactionListener = new InteractionListener();
            //                this.interactionListener.AddListener(this.OnReceiveMessage);
            //                this.unityAndroidBridge = new AndroidJavaClass("com.example.webviewplugins.UnityAndroidBridge");
            //                this.unityAndroidBridge.Call("setListener", this.interactionListener);
            //            }
            //#endif
            Debug.LogFormat("<><InteractionTest.AndroidCallback>Receive message from android: {0}", message);
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

    private void OnReceiveMessage(string message)
    {
        try
        {
            Debug.LogFormat("<><InteractionTest.OnReceiveMessage>Receive message from android: {0}", message);
            this.loopTimes += 1;
            if (!string.IsNullOrEmpty(message) && message.ToUpper() == "OK" && this.loopTimes <= this.loopTimesLimit)
            {
                this.EvaluateJavaScript();
            }
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.OnReceiveMessage>Error: {0}", ex.Message);
        }
    }

    private void EvaluateJavaScript()
    {
        try
        {
            if (this.JavaObjectActivity == null)
            {
                this.messageBox.text = "JavaObjectActivity is null";
                return;
            }

            this.JavaObjectActivity.Call("evaluateJavaScript");
        }
        catch (Exception ex)
        {
            this.messageBox.text = string.Format("<><InteractionTest.CallAndroidMethod>Error: {0}", ex.Message);
        }
    }
}

[Serializable]
public class WebInfo
{
    public string Url;
    public string JsFunction1;
    public string JsFunction2;
}
using System;
using System.Collections.Generic;
using UnityEngine;

public class InteractionListener : AndroidJavaProxy
{
    public InteractionListener() : base("com.example.webviewplugins.InteractionListener") { }
    private List<Action<string>> listeners = new List<Action<string>>();

    public void callback(string message)
    {
        if (this.listeners != null && this.listeners.Count > 0)
        {
            foreach (var listener in this.listeners)
            {
                try
                {
                    if (listener != null)
                    {
                        listener.Invoke(message);
                    }
                }
                catch (Exception ex)
                {
                    Debug.LogErrorFormat("<><InteractionListener.callback>Error: {0}", ex.Message);
                }
            }
        }
    }

    public void AddListener(Action<string> listener)
    {
        if (!this.listeners.Contains(listener))
        {
            this.listeners.Add(listener);
        }
    }

    public void RemoveListener(Action<string> listener)
    {
        if (!this.listeners.Contains(listener))
        {
            this.listeners.Remove(listener);
        }
    }
}

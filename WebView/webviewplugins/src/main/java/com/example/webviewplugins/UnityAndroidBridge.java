package com.example.webviewplugins;

public class UnityAndroidBridge {
    private static UnityAndroidBridge instance = null;
    private static InteractionListener interactionListener = null;

    public static UnityAndroidBridge getInstance() {
        if (instance == null) {
            instance = new UnityAndroidBridge();
        }
        return instance;
    }

    public UnityAndroidBridge() {
        System.out.println("create one UnityAndroidBridge instance");
    }

    public void setListener(InteractionListener listener) {
        interactionListener = listener;
        System.out.println("set interaction listener + + + + +");
    }

    public void sendMessageToUnity(final String message) {
        System.out.println(String.format("interactionListener is null: %s", interactionListener == null));
        if (interactionListener != null) {
            System.out.println("send message to unity: " + message);
            interactionListener.callback(message);
        }
    }
}

interface InteractionListener {
    void callback(String message);
}

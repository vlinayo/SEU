package com.example.vlinayo.webviewproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView myWebView = (WebView) findViewById(R.id.webview);
//        myWebView.loadUrl("http://192.168.1.128:8080/");
//        myWebView.loadUrl("http://192.168.1.130:8080/");
        myWebView.loadUrl("http://10.1.63.42:8080/");


//        myWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);



    }
}

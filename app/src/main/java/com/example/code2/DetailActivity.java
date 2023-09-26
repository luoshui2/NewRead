package com.example.code2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {


    private String url;
    private WebView webView;
    private WebSettings webSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        url = intent.getStringExtra(Constants.NEWS_DETAIL_URL_KEY);
        Log.d("flag2","url = "+url);
        //url = "https://www.1905.com/news/20230925/1642977.shtml";
        webView = findViewById(R.id.wv);
        webSettings = webView.getSettings();
        //进行网页的设置
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);


        webView.setWebViewClient(new WebViewClient(){
            //url的意图被拒绝
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            //使用js屏蔽信息
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                view.loadUrl("javascript:function setTop(){document.querySelector('body > div.m-nfwheader > div.m-shdmin > div').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('body > div.m-nfwheader > div.m-hd.m-xf > div').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('body > div.m-crm.g-wp > div > div').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('body > div.m-ft').style.display=\"none\";}setTop();");
                view.loadUrl("javascript:function setTop(){document.querySelector('body > div.m-content.g-wp.f-cb > div.main > div.m-more').style.display=\"none\";}setTop();");
            }
        });
        webView.loadUrl(url);
    }
}
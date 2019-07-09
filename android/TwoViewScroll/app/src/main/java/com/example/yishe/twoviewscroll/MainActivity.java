package com.example.yishe.twoviewscroll;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    View headerStub;
    RelativeLayout titleLayout;
    ObservableWebView webview;
    TextView titleText;

    long lastLayoutTime;
    int titleHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        webview.loadUrl("https://www.baidu.com/");
    }

    private void initView(){
        headerStub = findViewById(R.id.header_stub);
        titleLayout = findViewById(R.id.title_layout);
        webview = findViewById(R.id.webview);
        titleText = findViewById(R.id.title_text);

        webview.getSettings().setJavaScriptEnabled(true);

        lastLayoutTime = System.currentTimeMillis();
        headerStub.post(new Runnable() {
            @Override
            public void run() {
                titleHeight = headerStub.getHeight();
            }
        });

        webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback() {
            @Override
            public void onScroll(int x, int y, int dx, int dy) {
                if(System.currentTimeMillis() - lastLayoutTime < 100) return ; //防止频繁layout,界面抖动

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)headerStub.getLayoutParams();
                layoutParams.height = layoutParams.height - dy;
                if(layoutParams.height > titleHeight){
                    layoutParams.height = titleHeight;
                }else if(layoutParams.height < 0){
                    layoutParams.height = 0;
                }
                headerStub.requestLayout();
                lastLayoutTime = System.currentTimeMillis();

                float alpha = layoutParams.height/(titleHeight+ 0.0f);
                titleLayout.setAlpha(alpha);

            }
        });


        webview.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleText.setText(title);
            }
        });

    }



}

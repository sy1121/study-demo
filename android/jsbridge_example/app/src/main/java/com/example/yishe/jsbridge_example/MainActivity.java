package com.example.yishe.jsbridge_example;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;


public class MainActivity extends Activity {

    private EditText mEditText;
    private Button btnSend;
    private BridgeWebView mBridgeWebview;
    private Button btnReset;


    private MyHandlerCallBack.OnSendDataListener mOnSendDataListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViw();

        initListener();
        initWebView();

    }

    private void initViw(){
        mEditText = findViewById(R.id.edit_view);
        btnSend = findViewById(R.id.btn_send);
        mBridgeWebview = findViewById(R.id.bridge_webview);
        btnReset = findViewById(R.id.btn_reset);
    }


    private void initWebView() {
        //Handler做为通信桥梁的作用，接收处理来自H5数据及回传Native数据的处理，当h5调用send()发送消息的时候，调用MyHandlerCallBack
        mBridgeWebview.setDefaultHandler(new MyHandlerCallBack(mOnSendDataListener));
        //加载网页地址
        mBridgeWebview.loadUrl("file:///android_asset/web.html");

        //有方法名的都需要注册Handler后使用
        mBridgeWebview.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i("liuw", "html返回数据为:" + data);
                if (!TextUtils.isEmpty(data)) {
                    mEditText.setText("通过调用Native方法接收数据：\n" + data);
                }
                function.onCallBack("Native已经接收到数据：" + data + "，请确认！");
            }
        });

        mBridgeWebview.registerHandler("functionOpen", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Toast.makeText(MainActivity.this, "网页在打开你的文件预览", Toast.LENGTH_SHORT).show();
            }
        });

        //应用启动后初始化数据调用，js处理方法connectWebViewJavascriptBridge(function(bridge)
        mBridgeWebview.callHandler("functionInJs", "hahaha", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                mEditText.setText("向h5发送初始化数据成功，接收h5返回值为：\n" + data);
            }
        });

        mBridgeWebview.send("来自java的发送消息！！！", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Toast.makeText(MainActivity.this,"bridge.init初始化数据成功"+data,Toast.LENGTH_SHORT).show();
            }
        });

        mOnSendDataListener = new MyHandlerCallBack.OnSendDataListener() {
            @Override
            public void sendData(String data) {
                mEditText.setText("通过webview发消息接收到数据：\n" + data);
            }
        };

    }

    private void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = mEditText.getText().toString();
                //直接调用nativeFunction方法向H5发送数据
                mBridgeWebview.loadUrl("javascript:nativeFunction('" + data + "')");
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBridgeWebview.loadUrl("file:///android_asset/web.html");
            }
        });

    }

}

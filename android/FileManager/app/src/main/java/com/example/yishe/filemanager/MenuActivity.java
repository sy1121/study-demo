package com.example.yishe.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yishe.filemanager.View.FileInfoDialog;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.yishe.filemanager.R.id.copy_panel;

public class MenuActivity extends Activity {
    private  static final String TAG =MenuActivity.class.getSimpleName();
    public static final int DELETE_CANCEL_CODE =0;
    public static final int DELETE_CODE = 1;
    public static final int COPY_CODE = 2;
    public static final int CUT_CODE = 3;
    public static final int RENAME_CANCEL_CODE =4;
    public static final int RENAME_CODE= 5;
    public static final int PASTE_CODE = 6;
    public static final int CREATEFLODER_CODE=7;
    @BindView(R.id.paste_out_panel)
    LinearLayout paste_out_panel;
    @BindView(R.id.paste_panel)
    RelativeLayout pastePanel;
    @BindView(R.id.delete_panel)
    RelativeLayout deletePanel;
    @BindView(copy_panel)
    RelativeLayout copyPanel;
    @BindView(R.id.cut_panel)
    RelativeLayout cutPanel;
    @BindView(R.id.rename_panel)
    RelativeLayout renamePanel;
    @BindView(R.id.createFolder_panel)
    RelativeLayout createFolderPanel;
    @BindView(R.id.fileInfo_panel)
    RelativeLayout fileInfoPanel;
    String curFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        if(intent != null){
            curFilePath = intent.getStringExtra("path");
            Log.i(TAG,"curFilePath ="+ curFilePath);
        }
        initView();
    }


    private void initView(){
        deletePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });
        copyPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCopy();
            }
        });
        cutPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCut();
            }
        });
        renamePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRename();
            }
        });
        createFolderPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCreateFolder();
            }
        });
        fileInfoPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileInfo();
            }
        });
        if(null!=MainActivity.getWaittingCopyFile()){
            paste_out_panel.setVisibility(View.VISIBLE);
            pastePanel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doPaste();
                }
            });
        }
    }


    private void doDelete(){
        if(curFilePath.isEmpty()) return ;
        judgeDialog();


    }

    private  void judgeDialog(){
        AlertDialog.Builder builder =new AlertDialog.Builder(MenuActivity.this);
        File file = new File(curFilePath);
        builder.setMessage("确认删除"+file.getName()+"吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultCode(DELETE_CODE,null);
                dialog.dismiss();
                MenuActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultCode(DELETE_CANCEL_CODE,null);
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void doCopy(){
        if(curFilePath.isEmpty()) {
            Toast.makeText(MenuActivity.this,"没有选择文件！",Toast.LENGTH_SHORT).show();
            return ;
        }
        MainActivity.setWaittingCopyFile(new File(curFilePath));
        Toast.makeText(MenuActivity.this,"复制成功",Toast.LENGTH_SHORT).show();
        setResultCode(COPY_CODE,null);
        finish();
    }

    private void doCut(){
        if(curFilePath.isEmpty()){
            Toast.makeText(MenuActivity.this,"没有选择文件！",Toast.LENGTH_SHORT).show();
            return ;
        }
        MainActivity.setWaittingCopyFile(new File(curFilePath));
        Toast.makeText(this,"剪切成功",Toast.LENGTH_SHORT).show();
        setResultCode(CUT_CODE,null);
        finish();
    }

    private void doRename(){
        final EditText fileName = new EditText(this);
        File file = new File(curFilePath);
        fileName.setText(file.getName());
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("请输入新名称");
        builder.setView(fileName);
        fileName.setFocusable(true);
        fileName.setFocusableInTouchMode(true);
        builder.setPositiveButton("确认",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName =fileName.getText().toString();
                Log.i(TAG,"newName ="+newName);
                setResultCode(RENAME_CODE,newName);
                dialog.dismiss();
                MenuActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResultCode(RENAME_CANCEL_CODE,null);
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void doPaste(){
        setResultCode(PASTE_CODE,null);
        finish();
    }

    private void doCreateFolder(){
        setResultCode(CREATEFLODER_CODE,null);
        finish();
    }

    private void showFileInfo(){
        FileInfoDialog dialog = new FileInfoDialog(MenuActivity.this,R.style.dialog);
        File curFile = new File(curFilePath);
        dialog.setTitle(curFile.getName());
        dialog.setSize(getFileSizeString(curFile.length()));
        if(curFile.isFile()){
            dialog.setModifyTimePanelVisible(View.VISIBLE);
            dialog.setTime(getFormatTimeString(curFile.lastModified()));
        }
        dialog.setLocation(curFile.getAbsolutePath());
        dialog.setYesBtn(new FileInfoDialog.OnYesClickListener() {
            @Override
            public void onYesClick() {
                MenuActivity.this.finish();
            }
        },"确定");
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        dialog.show();
    }

    private String getFileSizeString(long size){
        String result="";
        DecimalFormat decimalFormat=new DecimalFormat(".00");
        if(size/1024/1024>0){
            float t= (size+0.1f)/1024/1024;
            result=decimalFormat.format(t)+"GB";
        }else if(size/1024>0){
            float t= (size+0.1f)/1024;
            result=decimalFormat.format(t)+"MB";
        }else {
            result =size+"KB";
        }
        return result;
    }

    private String getFormatTimeString(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = formatter.format(time);
        return dateString;
    }


    private void setResultCode(int code,String name){
        Intent intent = new Intent();
        intent.putExtra("return",code);
        intent.putExtra("name",name);
        setResult(RESULT_OK,intent);
    }
}

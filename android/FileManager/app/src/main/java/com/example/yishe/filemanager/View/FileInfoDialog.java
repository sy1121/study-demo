package com.example.yishe.filemanager.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yishe.filemanager.R;

/**
 * Created by yishe on 2017/9/23.
 */

public class FileInfoDialog extends Dialog {
    private static final String TAG="FileInfoDialog";
    private TextView title;
    private TextView  info_size;
    private TextView info_time;
    private TextView info_loacation;
    private Button yesBtn;
    private LinearLayout modifyTimePanel;
    private String yesStr="确定";

    private String fileName;
    private String fileSize;
    private String fileTime;
    private String fileLocation;

    private OnYesClickListener onYesClickListener;


    public FileInfoDialog(Context context,int theme){
        super(context,theme);
        initView();
    }

    public FileInfoDialog(Context context){
        super(context);
        initView();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        initData();
        initEvent();
    }

    private void initView(){
        setContentView(R.layout.file_info_dialog);
        title = (TextView)findViewById(R.id.file_name);
        info_size = (TextView)findViewById(R.id.fileinfo_size);
        info_time =(TextView)findViewById(R.id.fileinfo_time);
        info_loacation = (TextView)findViewById(R.id.fileinfo_location);
        yesBtn = (Button) findViewById(R.id.fileinfo_ok);
        modifyTimePanel =(LinearLayout)findViewById(R.id.modify_time_panel);
    }

    private void initData(){
        if(fileName!=null)  title.setText(fileName);
        if(fileSize!= null) info_size.setText(fileSize);
        if(fileTime!=null)  info_time.setText(fileTime);
        if(fileLocation!=null) info_loacation.setText(fileLocation);
        yesBtn.setText(yesStr);
    }

    private void  initEvent(){
        yesBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
               if(onYesClickListener!=null)
                   onYesClickListener.onYesClick();
            }
        });
    }


    public void setYesBtn(OnYesClickListener onYesClickListener,String yesStr){
        this.onYesClickListener =onYesClickListener;
        if(yesStr!=null&&!yesStr.isEmpty())
        this.yesStr = yesStr;
    }

    public void setTitle(String title){
        this.fileName = title;
    }

    public void setSize(String size){
        fileSize =size;
    }

    public void setTime(String time){
        fileTime = time;
    }

    public void setLocation(String location){
        fileLocation = location;
    }

    public void  setModifyTimePanelVisible(int visible){
        if(modifyTimePanel!=null)
            modifyTimePanel.setVisibility(visible);
    }



    public  interface  OnYesClickListener{
        public void  onYesClick();
    }

}

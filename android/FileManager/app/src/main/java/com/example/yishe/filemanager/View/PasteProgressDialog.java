package com.example.yishe.filemanager.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yishe.filemanager.R;

/**
 * Created by yishe on 2017/9/23.
 */

public class PasteProgressDialog extends Dialog {

    private static  final String  TAG = PasteProgressDialog.class.getSimpleName();

    private TextView count;
    private TextView fileName;
    private TextView progressNum;
    private ProgressView progressView;
    private Button cancel;

    private int sumFileCount;
    private int pastedFileCount;
    private String pastingFileName;
    private int curProgress;

    private IonCancelClickListener  cancelClickListener;

    public PasteProgressDialog(Context context){
        super(context);
        initView();
    }

    public PasteProgressDialog(Context context, int theme){
        super(context,theme);
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initEvent();
    }

    private void  initView(){
        setContentView(R.layout.paste_progress_dialog);
        count = (TextView) findViewById(R.id.paste_count_text);
        fileName =(TextView)findViewById(R.id.paste_file_name);
        progressNum = (TextView)findViewById(R.id.paste_progress_num);
        progressView = (ProgressView)findViewById(R.id.paste_progress_view);
        cancel = (Button)findViewById(R.id.fileinfo_cancel);
    }

    private void initData(){
        if(sumFileCount!=0){
            String info="正在处理("+pastedFileCount+"/"+sumFileCount+"):";
            count.setText(info);
        }
        fileName.setText(pastingFileName);
        progressNum.setText(curProgress+"%");
        progressView.setProgress(curProgress);
    }

    private void initEvent(){
        if(cancel!=null){
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cancelClickListener!=null)
                        cancelClickListener.onCancelClick();
                }
            });
        }
    }


    public void setSumFileCount(int sumFileCount) {
        this.sumFileCount = sumFileCount;
        if(sumFileCount!=0){
            String info="正在处理("+pastedFileCount+"/"+sumFileCount+"):";
            count.setText(info);
        }
    }

    public void setPastedFileCount(int pastedFileCount) {
        this.pastedFileCount = pastedFileCount;
        if(sumFileCount!=0){
            String info="正在处理("+pastedFileCount+"/"+sumFileCount+"):";
            count.setText(info);
        }
    }

    public void setPastingFileName(String pastingFileName) {
        this.pastingFileName = pastingFileName;
        //文件名过长，截取
        if(pastingFileName!=null)
            pastingFileName= pastingFileName.length()>18?pastingFileName.substring(0,18)+"...":pastingFileName;
        fileName.setText(pastingFileName);
    }

    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
        progressNum.setText(curProgress+"%");
        progressView.setProgress(curProgress);
    }



    public void setCancelClickListener(IonCancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public interface  IonCancelClickListener{
         public void onCancelClick();
    }


}

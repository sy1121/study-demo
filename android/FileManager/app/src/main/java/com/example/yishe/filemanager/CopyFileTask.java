package com.example.yishe.filemanager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.yishe.filemanager.View.PasteProgressDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.example.yishe.filemanager.R.style.dialog;

/**
 * Created by yishe on 2017/9/21.
 */

public class CopyFileTask extends AsyncTask<String,Integer,Integer>{
    private static final String TAG=CopyFileTask.class.getSimpleName();
    //private ProgressDialog progressDialog;
    private PasteProgressDialog pasteProgressDialog;
    private Context context;
    private Handler handler;
    private long totalSize;
    private long copySize;
    private String curFileName;
    private int sumFileCount;
    private int finishedFileCount;
    public CopyFileTask(Context context,Handler  handler) {
        super();
        this.context = context;
       /* progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在复制...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);*/
        pasteProgressDialog = new PasteProgressDialog(context, dialog);
        // 设置dialog 居中显示
        Window dialogWindow = pasteProgressDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        pasteProgressDialog.setCancelClickListener(new PasteProgressDialog.IonCancelClickListener() {
            @Override
            public void onCancelClick() {
                CopyFileTask.this.cancel(true); //结束Task
                pasteProgressDialog.dismiss();
            }
        });
        this.handler = handler;
        totalSize = 0L;
        copySize =0L;
        curFileName = "";
        sumFileCount =0;
        finishedFileCount = 0;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pasteProgressDialog.show();
    }

    @Override
    protected Integer doInBackground(String... params) {
        Log.i(TAG,"doInBackground");
        String oldFilePath =params[0];
        String newFilePath = params[1];
        Log.i(TAG,"oldFilePath="+oldFilePath+" newFilePath="+newFilePath);
        File oldFile = new File(oldFilePath);
        totalSize = getFileSize(oldFile);
        sumFileCount  = getFileCount(oldFile);
        if(oldFile.isFile()){
            copyFile(oldFilePath,newFilePath);
        }else{
            copyFolder(oldFilePath,newFilePath);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pasteProgressDialog.setCurProgress(values[0]);
        pasteProgressDialog.setPastingFileName(curFileName);
        pasteProgressDialog.setPastedFileCount(finishedFileCount);
        pasteProgressDialog.setSumFileCount(sumFileCount);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        Log.i(TAG,"onPostExecute");
        pasteProgressDialog.dismiss();
        handler.sendEmptyMessage(1); // 刷新列表
        super.onPostExecute(integer);
    }



    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            oldfile.length();
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                long length =oldfile.length();
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                    publishProgress((int)(((bytesum+0.1f)/length)*100));
                }
                inStream.close();
                finishedFileCount++;
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    /**
     * 复制整个文件夹内容
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a=new File(oldPath);
            String[] file=a.list();
            File temp=null;
            Log.i(TAG,"sunSize="+totalSize);
            for (int i = 0; i < file.length; i++) {
                if(oldPath.endsWith(File.separator)){
                    temp=new File(oldPath+file[i]);
                }else{
                    temp=new File(oldPath+File.separator+file[i]);
                }

                if(temp.isFile()){
                    curFileName = temp.getName();
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ( (len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                        copySize+=len;
                        Log.i(TAG,"copySize ="+ copySize);
                        Log.i(TAG,"progress ="+ (int)((copySize+0.1f)/totalSize*100));
                        publishProgress((int)((copySize+0.1f)/totalSize*100));
                    }
                    output.flush();
                    output.close();
                    input.close();
                    finishedFileCount++;
                    if(isCancelled()) break;
                }
                if(temp.isDirectory()){//如果是子文件夹
                    copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    /*** 获取文件夹大小 ***/
    public long getFileSize(File f) {
        long size = 0;
        try {
            File[] flist = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFileSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取文件个数
     */
    public int getFileCount(File f){
        int count=0;
        if(f.isFile()) return 1;
        try{
            File[] files  = f.listFiles();
            for(int i =0;i<files.length;i++){
                if(files[i].isDirectory()){
                    count= count+getFileCount(files[i]);
                }else{
                    count = count +1;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return count;
    }

}

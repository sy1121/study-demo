package com.example.yishe.filemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yishe.filemanager.Adapter.FileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.example.yishe.filemanager.R.id.showtv;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MENU_REQUEST_CODE = 1;
    private ListView lv;
    private TextView tv;

    private FileAdapter fileAdapter;
    private List<File> datas;
    private Stack<String> paths;
    private File[] files;

    private int foucusIndex =-1;

    private long lastPressBackTime;

    private static File waittingCopyFile;

    private int sameNameCount=0;

    private int   operate;

    private static final int MSG_REFRESH_LIST=1;
    private Handler handler;

    class MyUiHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            try{
                switch(msg.what){
                    case MSG_REFRESH_LIST:
                        showChange(getPathString());
                        doLast(operate);
                        break;
                    default:
                        break;
                }
            }catch (Exception e){
                Log.i(TAG,"messsge error!");
            }
        }
    }

    private void doLast(int operate ){
        if(operate ==2){ //剪切
            // 删除原来的文件
            deleteFile(waittingCopyFile);
            Toast.makeText(MainActivity.this,"剪切完成",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"复制完成",Toast.LENGTH_SHORT).show();
        }
        waittingCopyFile =null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        handler = new MyUiHandler();
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        tv = (TextView) findViewById(showtv);
        paths = new Stack<String>();
        datas = new ArrayList<>();
        Log.i(TAG,"rootPath="+Environment.getExternalStorageDirectory().toString());
        String rootPath = Environment.getExternalStorageDirectory().toString();
        Log.i(TAG,"file count ="+new File(rootPath).length());
        paths.push(rootPath);
        files = Environment.getExternalStorageDirectory().listFiles();
        for (File file : files) {
            datas.add(file);
        }
        tv.setText(getPathString());
        fileAdapter = new FileAdapter(this, datas);
        lv.setAdapter(fileAdapter);
        lv.setOnItemClickListener(new FileItemClickListener());
        lv.setSelector(R.drawable.itembackground);
        lv.requestFocus();
        lv.setOnItemSelectedListener(new FileListItemFoucusChangeListner());

    }

    private String getPathString() {
        String results = "";
        Stack<String> temp = new Stack<String>();
        temp.addAll(paths);
        while (!temp.isEmpty()) {
            results = temp.pop() + results;
        }
        return results;
    }


    private class FileItemClickListener implements AdapterView.OnItemClickListener {

        @Override

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File currentFile = files[position];
            if (currentFile.isFile()) {
                //尝试打开文件
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri data = Uri.fromFile(currentFile);
                int index = currentFile.getName().lastIndexOf(".");
                String suffix = currentFile.getName().substring(index + 1);
                String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
                intent.setDataAndType(data, type);
                startActivity(intent);
            } else {
                //进入下一级目录
                paths.push("/" + currentFile.getName());
                Log.i(TAG, "current path=" + getPathString());
                showChange(getPathString());
            }
        }
    }

    private class FileListItemFoucusChangeListner implements  AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG,"select Position ="+position);
            foucusIndex = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            foucusIndex = -1;
        }
    }

    private void showChange(String path) {
        tv.setText(path);
        files = new File(path).listFiles();
        datas.clear();
        for (File file : files) {
            datas.add(file);
        }
        fileAdapter.setDatas(datas);
        if (datas.isEmpty()) {
            TextView empty_text = (TextView) findViewById(R.id.empty_text);
            empty_text.setVisibility(View.VISIBLE);
        } else {
            TextView empty_text = (TextView) findViewById(R.id.empty_text);
            empty_text.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        String pathStr = getPathString();
        String rootPath = Environment.getExternalStorageDirectory().toString();
        Log.i(TAG, "rootPath= " + rootPath + " pathStr= " + pathStr);

        if (pathStr.length() > rootPath.length()) {
            paths.pop();
            pathStr = getPathString();
            tv.setText(pathStr);
            showChange(pathStr);
        } else {
            if (System.currentTimeMillis() - lastPressBackTime < 1000) {
                Toast.makeText(this, "退出应用", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_LONG).show();
            }
        }
        lastPressBackTime = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "keycode =" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                try {
                    Intent i = new Intent(this, MenuActivity.class);
                    Log.i(TAG, "path = " + getCurrentFileName());
                    i.putExtra("path", getCurrentFileName());
                    startActivityForResult(i, MENU_REQUEST_CODE);
                } catch (Exception e) {

                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            default: {
                break;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MENU_REQUEST_CODE) {
            if (data != null) {
                int code = data.getIntExtra("return", 0);
                String newName = data.getStringExtra("name");
                switch (code) {
                    case MenuActivity.DELETE_CODE:
                         doDelete();
                        break;
                    case MenuActivity.COPY_CODE:
                         doCopy();
                        break;
                    case MenuActivity.CUT_CODE:
                        doCut();
                        break;
                    case MenuActivity.RENAME_CODE:
                        doRename(newName);
                        break;
                    case MenuActivity.PASTE_CODE:
                        doPaste();
                        break;
                    case MenuActivity.CREATEFLODER_CODE:
                        doCreateFolder();
                    default:break;
                }

            }
        }
    }

    private void doDelete(){
        File curFile = files[foucusIndex];
        deleteFile(curFile);
        datas.remove(foucusIndex);
        fileAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT);
    }

    private void deleteFile(File file){
        try{
            if(file.isFile()){
                file.delete();
            }else{
                File[] childFile = file.listFiles();
                if(childFile == null || childFile.length == 0){
                    file.delete();
                    return;
                }
                for(File f : childFile){
                    deleteFile(f);
                }
                file.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void doCopy(){
        operate=1;
    }

    private void   doCut(){
        operate=2;
    }

    private void  doRename(String newName){
        Log.i(TAG,"newName="+newName);
        File newFile=new File(getPathString()+"/"+newName);
        File oldFile= files[foucusIndex];
        oldFile.renameTo(newFile);
        Toast.makeText(MainActivity.this,"重命名成功",Toast.LENGTH_SHORT).show();
        showChange(getPathString());
    }

    private void doPaste(){
        if(waittingCopyFile==null) return;
        String curDirPath = getPathString();
        boolean isSame=checkSameNameFile(curDirPath,waittingCopyFile);
        if(!isSame)
            new CopyFileTask(MainActivity.this,handler).execute(waittingCopyFile.getPath(),curDirPath+File.separator+waittingCopyFile.getName());

    }

    private boolean checkSameNameFile(final String curDirPath,File file){
        boolean hasSameNameFile = false;
       // int sameNameCount=0;
        String fileName = file.getName();
        File curDir = new File(curDirPath);
        File[] fileList = curDir.listFiles();
        if(file.isFile()){
            for(File f:fileList){
                if(!f.isFile())  continue;
                String tempName = f.getName().contains("(")?f.getName().substring(0,f.getName().indexOf("(")):f.getName();
                if(tempName.equals(fileName)){
                    hasSameNameFile=true;
                    sameNameCount++;
                }
            }
        }else{
            for(File f:fileList){
                if(f.isFile())  continue;
                String tempName = f.getName().contains("(")?f.getName().substring(0,f.getName().indexOf("(")):f.getName();
                if(tempName.equals(fileName)){
                    hasSameNameFile=true;
                    sameNameCount++;
                }
            }
        }

        if(hasSameNameFile){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("是否创建相同的文件/文件夹?");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.i(TAG,"sameNameCount ="+ sameNameCount);
                    new CopyFileTask(MainActivity.this,handler).execute(waittingCopyFile.getPath(),curDirPath+File.separator+waittingCopyFile.getName()+"("+sameNameCount+")");
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);
            Dialog dialog = builder.create();
            dialog.show();
        }
        return hasSameNameFile;
    }

    private void doCreateFolder(){
        File file = new File(getPathString()+File.separator+"新建文件夹");
        int index =1;
        while(file.exists()){
            file =new File(getPathString()+File.separator+"新建文件夹("+index+")");
            index++;
        }
        file.mkdirs();
        showChange(getPathString());
        Toast.makeText(MainActivity.this,"新建文件夹成功",Toast.LENGTH_SHORT).show();
    }




    private String getCurrentFileName() {
        String fileName = "";
        int position = foucusIndex;
       // int position = getCurrentFocus();
        if (position == -1)
            return fileName;
        else {
            File curFile = files[position];
            return getPathString() + "/" + curFile.getName();
        }
    }


    public static File getWaittingCopyFile() {
        return waittingCopyFile;
    }

    public static void setWaittingCopyFile(File waittingCopy) {
        waittingCopyFile = waittingCopy;
    }
}

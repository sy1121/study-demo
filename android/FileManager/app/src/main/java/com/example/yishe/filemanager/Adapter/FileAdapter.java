package com.example.yishe.filemanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yishe.filemanager.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yishe on 2017/9/20.
 */

public class FileAdapter  extends BaseAdapter{
    private static final String TAG="FileAdapter";

    private Context context;
    private List<File> files;
    private LayoutInflater layoutInflater;
    public FileAdapter(Context context, List<File> datas){
        this.context = context;
        files = datas;
        layoutInflater =LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file  =files.get(position);
        ViewHolder viewHolder;
        if(convertView ==null){
            convertView = layoutInflater.inflate(R.layout.listitem,null);
            viewHolder =new ViewHolder();
            viewHolder.fileIcon = (ImageView)convertView.findViewById(R.id.file_icon);
            viewHolder.createTime =(TextView)convertView.findViewById(R.id.file_create_time);
            viewHolder.fileName  = (TextView)convertView.findViewById(R.id.file_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(file.isDirectory()){
            viewHolder.fileIcon.setImageResource(R.mipmap.folder);
        }else{
            viewHolder.fileIcon.setImageResource(R.mipmap.file);
        }
        viewHolder.fileName.setText(file.getName());
        long time = file.lastModified();
        String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));
        viewHolder.createTime.setText(ctime);
        return convertView;
    }

    public void setDatas(List<File> newDatas){
        files =newDatas;
        notifyDataSetChanged();
    }

    public class ViewHolder{
        private ImageView fileIcon;
        private TextView fileName;
        private TextView createTime;
    }
}

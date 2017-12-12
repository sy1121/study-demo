package com.example.yishe.testfragment.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yishe.testfragment.MainActivity;
import com.example.yishe.testfragment.R;

/**
 * Created by yishe on 2017/12/12.
 */

public class TitleFragment extends Fragment{

    private String haha="";

    public TitleFragment(){
        super();
        Log.i("hahaha","TitleFragment-->TitleFragment()");

    }
    @Override
    public void onAttach(Context context) {
        Log.i("hahaha","TitleFragment-->onAttach()");
        super.onAttach(context);
        haha = ((MainActivity)context).getHahaString();
        Log.i("hahaha","haha="+haha);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("hahaha","TitleFragment-->onCreateView()");
        View view = inflater.inflate(R.layout.title_fragment_layout,null);
        TextView textView = (TextView) view.findViewById(R.id.tips_text);
        if(null!=textView){
            textView.setText(haha);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("hahaha","TitleFragment-->onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.i("hahaha","TitleFragment-->onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("hahaha","TitleFragment-->onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("hahaha","TitleFragment-->onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("hahaha","TitleFragment-->onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i("hahaha","TitleFragment-->onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("hahaha","TitleFragment-->onDetach()");
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Log.i("hahaha","TitleFragment-->onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("hahaha","TitleFragment-->onCreate()");
        super.onCreate(savedInstanceState);
    }
}

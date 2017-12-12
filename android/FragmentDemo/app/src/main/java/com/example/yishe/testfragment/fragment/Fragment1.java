package com.example.yishe.testfragment.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.yishe.testfragment.R;

/**
 * Created by yishe on 2017/12/12.
 */

public class Fragment1 extends Fragment implements View.OnClickListener{

    public interface OnBtn2ClickLintener{
        void onBtn2Click();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment1_layout,null);
        Button btn1  =(Button) view.findViewById(R.id.btn);
        btn1.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof OnBtn2ClickLintener){
            ((OnBtn2ClickLintener)getActivity()).onBtn2Click();
        }
    }
}

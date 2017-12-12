package com.example.yishe.testfragment.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.yishe.testfragment.MainActivity;
import com.example.yishe.testfragment.R;
import com.example.yishe.testfragment.utils.FragmentCallback;

/**
 * Created by yishe on 2017/12/12.
 */

public class ContentFragment extends Fragment implements View.OnClickListener{

    private TextView textView;
    private TextView textView2;
    private Button btn2;
    private FragmentCallback mCallback;

    public OnFragmentBtn1ClickListener getOnFragmentBtn1ClickListener() {
        return onFragmentBtn1ClickListener;
    }

    public void setOnFragmentBtn1ClickListener(OnFragmentBtn1ClickListener onFragmentBtn1ClickListener) {
        this.onFragmentBtn1ClickListener = onFragmentBtn1ClickListener;
    }

    private OnFragmentBtn1ClickListener onFragmentBtn1ClickListener;


    public interface OnFragmentBtn1ClickListener{
        void onBtnClick();
    }


    public ContentFragment(){
        super();
        Log.i("hahaha","ContentFragment-->ContentFragment");
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback =((MainActivity)context);
        Log.i("hahaha","ContentFragment-->onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("hahaha","ContentFragment-->onCreate");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("hahaha","ContentFragment-->onStart");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("hahaha","ContentFragment-->onActivityCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("hahaha","ContentFragment-->onCreateView");
        View view = inflater.inflate(R.layout.content_fragment_layout,null);
        textView =(TextView)view.findViewById(R.id.content_text);
        Bundle bundle = getArguments();
        String parent = bundle.getString("parent");
        if(textView!=null){
            textView.setText(parent);
        }
        String message=mCallback.getMessage();
        textView2 =(TextView)view.findViewById(R.id.content_text_2);
        if(textView2!=null){
            textView2.setText(message);
        }

        btn2 = (Button)view.findViewById(R.id.content_btn_2);
        btn2.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        Log.i("hahaha","ContentFragment-->onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i("hahaha","ContentFragment-->onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("hahaha","ContentFragment-->onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i("hahaha","ContentFragment-->onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("hahaha","ContentFragment-->onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i("hahaha","ContentFragment-->onDetach");
        super.onDetach();
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.content_btn_2){
            if(null!=onFragmentBtn1ClickListener){
                Log.i("hahaha","contentFragment onCreate");
                onFragmentBtn1ClickListener.onBtnClick();
            }
        }
    }
}

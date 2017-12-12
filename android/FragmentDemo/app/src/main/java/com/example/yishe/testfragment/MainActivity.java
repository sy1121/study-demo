package com.example.yishe.testfragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.yishe.testfragment.fragment.ContentFragment;
import com.example.yishe.testfragment.fragment.Fragment1;
import com.example.yishe.testfragment.fragment.Fragment2;
import com.example.yishe.testfragment.utils.FragmentCallback;

public class MainActivity extends AppCompatActivity implements FragmentCallback,Fragment1.OnBtn2ClickLintener,ContentFragment.OnFragmentBtn1ClickListener{

    private android.support.v4.app.FragmentManager fragmentManager;
    private ContentFragment contentFragment;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("hahaha","MainActivity -->onCreate ");
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction =fragmentManager.beginTransaction();
        contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("parent","MainActivity");
        contentFragment.setArguments(bundle);
        transaction.replace(R.id.frag_container,contentFragment);
        transaction.commit();
        contentFragment.setOnFragmentBtn1ClickListener(this);
        fragment1  = new Fragment1();
        fragment2  = new Fragment2();
        Log.i("hahaha","MainActivity -->onCreate1 ");
    }


    public String getHahaString(){
        return "haha";
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("hahaha","MainActivity -->onStart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("hahaha","MainActivity -->onResume ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("hahaha","MainActivity -->onPause ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("hahaha","MainActivity -->onStop ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("hahaha","MainActivity -->onDestroy ");
    }

    @Override
    public String getMessage() {
        return "FragmentCallback";
    }

    @Override
    public void onBtn2Click() {
        FragmentTransaction tx = fragmentManager.beginTransaction();
        tx.replace(R.id.frag_container,fragment2);
        tx.addToBackStack(null);
        tx.commit();

    }

    @Override
    public void onBtnClick() {
        Log.i("hahaha","MainActivity-->  onBtnClick");
        FragmentTransaction transaction=  fragmentManager.beginTransaction();
        transaction.hide(contentFragment);
        transaction.add(R.id.frag_container,fragment1,"frag1");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

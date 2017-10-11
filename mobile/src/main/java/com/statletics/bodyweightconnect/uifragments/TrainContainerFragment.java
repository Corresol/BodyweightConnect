package com.statletics.bodyweightconnect.uifragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.statletics.bodyweightconnect.R;

/**
 * Created by Tonni on 07.10.2016.
 */

public class TrainContainerFragment extends BaseContainerFragment {

    private boolean mIsViewInited;
    private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("test", "tab 1 oncreateview");
        return inflater.inflate(R.layout.container_fragment, null);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("test", "tab 1 container on activity created");
        if (!mIsViewInited) {
            mIsViewInited = true;
            initView();
        }
    }


    private void initView() {
        Log.e("test", "tab 1 init view");
        if(fragment!=null){
            replaceFragment(fragment,false);
        }else {
            replaceFragment(new ChooseFragment(), false);
        }
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        super.replaceFragment(fragment, addToBackStack);
        this.fragment=fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null){
            fragment.onSaveInstanceState(outState);
        }
    }

    public Fragment getCurrentFragment() {
        return fragment;
    }
}

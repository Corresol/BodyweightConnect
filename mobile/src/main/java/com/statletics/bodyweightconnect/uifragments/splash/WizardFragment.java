package com.statletics.bodyweightconnect.uifragments.splash;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.statletics.bodyweightconnect.R;

/**
 * Created by Tonni on 03.12.2016.
 */

@SuppressLint("ValidFragment")
public class WizardFragment extends Fragment {
    int wizard_page_position=0;

    public WizardFragment() {
    }

    public WizardFragment(int position) {
        this.wizard_page_position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = R.layout.splash_page1;
        String page= "";
        switch (wizard_page_position) {
            case 0:
                layout_id = R.layout.splash_page1;
                page="splash1";
                break;

            case 1:
                layout_id = R.layout.splash_page1;
                page="splash2";
                break;
            case 2:
                layout_id = R.layout.splash_page1;
                page="splash3";
                break;
            case 3:
                layout_id = R.layout.splash_page1;
                page="splash4";
                break;
        }
        View v = inflater.inflate(layout_id, container, false);
        WebView webview = (WebView)v.findViewById(R.id.splashpage1);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("file:///android_asset/www/splash/"+page+".html");

        return v;
    }

}

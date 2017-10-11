package com.statletics.bodyweightconnect.uifragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.network.NetUtil;

/**
 * Created by Tonni on 10.08.2016.
 */
public class StatleticsFragment extends Fragment {

    private WebView webview;
    private Bundle webViewBundle;
    private String url = "https://bwcnnct.com/median/wo_median.php";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.tab_statletics_fragment,container,false);

        webview = (WebView) view.findViewById(R.id.statsWebView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setSupportMultipleWindows(false);
        webview.getSettings().setUseWideViewPort(true);
        webview.setScrollContainer(false);

        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                getActivity().setProgress(progress * 1000);
            }

        });

        webview.setWebViewClient(new WebViewClient());
        if (webViewBundle != null) {
            webview.restoreState(webViewBundle);
        }

        if(NetUtil.hasNetworkConnect(this.getContext())) {
            webview.loadUrl(url);
        }else{
            webview.loadUrl("file:///android_asset/www/noconnection.html");
        }

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
        webViewBundle = new Bundle();
        webview.saveState(webViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

}

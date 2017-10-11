package com.statletics.bodyweightconnect.uifragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.statletics.bodyweightconnect.Constants;
import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.exception.NoDeviceException;
import com.statletics.bodyweightconnect.network.NetUtil;
import com.statletics.bodyweightconnect.type.DataHolder;
import com.statletics.bodyweightconnect.type.DeviceType;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.BWCFlicBroadcastReceiver;
import com.statletics.bodyweightconnect.util.JSBridge;
import com.statletics.bodyweightconnect.util.WearableManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.flic.lib.AppCredentialsNotProvidedException;
import io.flic.lib.FlicAppNotInstalledException;
import io.flic.lib.FlicBroadcastReceiverFlags;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;
import io.flic.lib.FlicManagerInitializedCallback;

/**
 * Created by Tonni on 10.08.2016.
 */
public class WebFragment extends Fragment {

    private WebView webview = null;
    private DataHolder dh;
    private FlicManager flicManager;
    private FlicButton button;
    private JSBridge bridge;
    private Bundle webViewBundle;
    //private WearableManager wearableManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("view Web fragment");
        View view = inflater.inflate(R.layout.tab_web_fragment, container, false);

//        Intent myIntent = getActivity().getIntent();
//        dh = (DataHolder) myIntent.getSerializableExtra(Constants.DATA_FOR_INTENT);


        dh = (DataHolder) getArguments().get(Constants.DATA_FOR_INTENT);

        ActivityManager.getInstance().setCurrentActivity(getActivity());
        webview = (WebView) view.findViewById(R.id.webView);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setSupportMultipleWindows(false);
        webview.getSettings().setUseWideViewPort(true);
        webview.setScrollContainer(false);

        CookieSyncManager.createInstance(getContext());

        final Activity activity = getActivity();
        webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }

        });

        webview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieSyncManager.getInstance().sync();
                //super.onPageFinished(view, url)
                injectScriptFile(view, "js/JSPlugin.js"); // see below ...
                if (dh.getUrl().toLowerCase().contains("freeletics")) {
                    Log.i("ScriptInjection","Inject freeletics javascript");
                    injectScriptFile(view, "js/jquery-1.12.4.min.js"); // see below ...
                    injectScriptFile(view, "js/FreeleticsScript.js"); // see below ...
                    view.loadUrl("javascript:setTimeout(placeOnClickEventHandler(), 500)");
                } else if (dh.getUrl().toLowerCase().contains("madbarz")) {
                    Log.i("ScriptInjection","Inject madbarz javascript");
                    injectScriptFile(view, "js/MadbarzScript.js"); // see below ...
                    view.loadUrl("javascript:setTimeout(placeOnClickEventHandler(), 500)");
                }else{
                    Log.i("ScriptInjection","Inject no javascript");
                }
                // test if the script was loaded
                view.loadUrl("javascript:setTimeout(test(), 500)");
                //view.loadUrl("javascript:setTimeout(placeOnClickEventHandler(), 500)");

                Log.e("URL","URL::"+url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if(dh.getUrl().contains("freeletics")){
                    if(url.contains("freeletics.com/api/bodyweight/")) {
                        Log.e("URL Resource", "URL::" + url);
                        view.loadUrl("javascript:setTimeout(placeOnClickEventHandler(), 200)");
                    }
                }else if(dh.getUrl().contains("madbarz")){
                    if(url.contains("madbarz.com/")) {
                        Log.e("URL Resource", "URL::" + url);
                        view.loadUrl("javascript:setTimeout(placeOnClickEventHandler(), 200)");
                    }
                }

            }
        });

        bridge = new JSBridge(getActivity().getApplicationContext(), webview);
        webview.addJavascriptInterface(bridge, "JSBridgePlugin");

        if (webViewBundle != null) {
            webview.restoreState(webViewBundle);
            webview.loadUrl(webViewBundle.getString("url"));
            dh = (DataHolder) webViewBundle.getSerializable("dh");
        } else {


            if (dh.getType().equals(DeviceType.FLIC)) {
                configureFlic();
            } else if (dh.getType().equals(DeviceType.GAMEPAD)) {
                try {
                    configureGamepad();
                } catch (NoDeviceException e) {
                    e.printStackTrace();
                }
            } else if (dh.getType().equals(DeviceType.WEAR)) {
                System.out.println("Load WearableManager..");
                WearableManager wearableManager = new WearableManager();
            }

            if(NetUtil.hasNetworkConnect(this.getContext())) {
                webview.loadUrl(dh.getUrl());
            }else{
                webview.loadUrl("file:///android_asset/www/noconnection.html");
            }
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
        webViewBundle = new Bundle();
        webview.saveState(webViewBundle);
        webViewBundle.putSerializable("dh",dh);
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    public void onDestroy() {
        System.out.println("destroy..");
        if (dh!=null && dh.getType().equals(DeviceType.FLIC)) {
            unregisterFlic();
        }
        super.onDestroy();
    }

    private void injectScriptFile(WebView view, String scriptFile) {
        InputStream input;
        try {
            input = getActivity().getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void callClickAction(View view) {
        if (webview != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("call Click Button......");
                    webview.loadUrl("javascript:clickBWCButton()");
                }
            });
            if(dh.getType()==DeviceType.WEAR){
                //send message
                System.out.println("send to wear...");
            }
        }
    }

    private void configureFlic() {
        ComponentName receiver = new ComponentName(getActivity().getApplicationContext(), BWCFlicBroadcastReceiver.class);
        getActivity().getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        FlicManager.setAppCredentials("Bodyweight Connect", "7a87330c-6b77-4760-8a63-f240da9e752d", "Bodyweight Connect");
        try {
            FlicManager.getInstance(getActivity(), new FlicManagerInitializedCallback() {
                @Override
                public void onInitialized(FlicManager manager) {
                    manager.initiateGrabButton(WebFragment.this.getActivity());
                    flicManager = manager;
                }
            });
        } catch (FlicAppNotInstalledException err) {
            Toast.makeText(this.getContext(), "Flic App is not installed", Toast.LENGTH_SHORT).show();
        } catch (AppCredentialsNotProvidedException ex) {
            Toast.makeText(this.getContext(), "Flic App AppCredentials check error / Check Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        FlicManager.getInstance(getActivity(), new FlicManagerInitializedCallback() {
            @Override
            public void onInitialized(FlicManager manager) {
                button = manager.completeGrabButton(requestCode, resultCode, data);
                if (button != null) {
                    button.registerListenForBroadcast(FlicBroadcastReceiverFlags.UP_OR_DOWN | FlicBroadcastReceiverFlags.REMOVED);
                    Toast.makeText(WebFragment.this.getContext(), "Grabbed a button", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WebFragment.this.getContext(), "Did not grab any button", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void configureGamepad() throws NoDeviceException {

        ArrayList gameControllerDeviceIds = new ArrayList();
        int[] deviceIds = InputDevice.getDeviceIds();
        for (int deviceId : deviceIds) {
            InputDevice dev = InputDevice.getDevice(deviceId);
            int sources = dev.getSources();

            // Verify that the device has gamepad buttons, control sticks, or both.
            if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                    || ((sources & InputDevice.SOURCE_JOYSTICK)
                    == InputDevice.SOURCE_JOYSTICK)) {
                // This device is a game controller. Store its device ID.
                if (!gameControllerDeviceIds.contains(deviceId)) {
                    gameControllerDeviceIds.add(deviceId);
                }
            }
        }
        if (gameControllerDeviceIds.size() < 1) {
            throw new NoDeviceException("No Device connected.");
        }
        ;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        System.out.println("ev:" + ev.toString());
        if ((ev.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            Toast.makeText(WebFragment.this.getContext(), ev.toString(), Toast.LENGTH_SHORT).show();
        }
        //return super.dispatchGenericMotionEvent(ev);
        return true;
    }

    public boolean dispatchKeyEvent(KeyEvent ev) {
        System.out.println("ev:" + ev.toString());
        if ((ev.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
            webview.goBack();
            return true; //I have tried here true also
        }
        if ((ev.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
            Toast.makeText(WebFragment.this.getContext(), ev.getKeyCode(), Toast.LENGTH_SHORT).show();
        }
        //return super.dispatchKeyEvent(ev);
        return true;
    }

    public boolean isWearDevice(){
        if(dh!=null){
            return (dh.getType()==DeviceType.WEAR);
        }
        return false;
    }

    public boolean isFlicDevice(){
        if(dh!=null){
            return (dh.getType()==DeviceType.FLIC);
        }
        return false;
    }


    private void unregisterFlic() {
        if (flicManager != null && button!=null) {
            button.removeAllFlicButtonCallbacks();
            flicManager.forgetButton(button);
        }
        FlicManager.destroyInstance();

        ComponentName receiver = new ComponentName(getContext().getApplicationContext(), BWCFlicBroadcastReceiver.class);
        getContext().getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}

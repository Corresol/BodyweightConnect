package com.statletics.bodyweightconnect.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.statletics.bodyweightconnect.WebActivity;
import com.statletics.bodyweightconnect.metronom.Metronom;
import com.statletics.bodyweightconnect.uifragments.TrainContainerFragment;
import com.statletics.bodyweightconnect.uifragments.WebFragment;

import java.util.Locale;

/**
 * Created by zenny on 12/8/2015.
 */
public class JSBridge {
    private TextToSpeech t1;

    private Context mContext;
    private WebView _webView;
    private Metronom metronom;
    private SharedPreferences sharedPref;
    private boolean play = false;

    /** Instantiate the interface and set the context */
    public JSBridge(Context c, WebView webView) {
        mContext = c;
        _webView=webView;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(c);

        t1=new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        metronom = new Metronom();
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }


    @JavascriptInterface
    public void Text2Speech(String dialogMsg){
        t1.speak(dialogMsg, TextToSpeech.QUEUE_FLUSH, null);
    }

    @JavascriptInterface
    public void sendText(String dialogMsg){
        Activity ac = ActivityManager.getInstance().getCurrentActivity();
        if (ac instanceof WebActivity) {
            WebActivity wac = (WebActivity) ac;
            if (((TrainContainerFragment) wac.getSupportFragmentManager().findFragmentByTag("train")).getCurrentFragment() instanceof WebFragment) {
                WebFragment wf = (WebFragment) ((TrainContainerFragment) wac.getSupportFragmentManager().findFragmentByTag("train")).getCurrentFragment();
                if (wf.isWearDevice()) {
                    new WearableManager().sendData("{ 'text' : '" + dialogMsg + "' }");
                }
            }
        }
    }


    @JavascriptInterface
    public void startMetronom(String exercise, String page){
        if(play){
            stopMetronom();
        }
        System.out.println("Start Metronom");
        float metronomTime = sharedPref.getFloat(ExterciseTranslator.translateToKey(exercise,page), 0);
        if(metronomTime==0){
            //fallback auf alte werte in mehrzahl
            metronomTime = sharedPref.getFloat(ExterciseTranslator.translateToKey(exercise+"s",page), 0);
        }
        System.out.println("Metronom Time for >"+exercise+"< : "+metronomTime+"s");
        if(metronomTime>0){
            metronom.setBpm(60/metronomTime);
            metronom.play();
            play=true;
        }

    }

    @JavascriptInterface
    public void stopMetronom()
    {
        if(play) {
            metronom.stop();
            play=false;
        }
    }


    public void close(){
        t1.stop();
        t1.shutdown();
    }
}
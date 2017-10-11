package com.statletics.bodyweightconnect;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.statletics.bodyweightconnect.type.StaticURLs;
import com.statletics.bodyweightconnect.uifragments.MapFragment;
import com.statletics.bodyweightconnect.uifragments.StatleticsFragment;
import com.statletics.bodyweightconnect.uifragments.TrainContainerFragment;
import com.statletics.bodyweightconnect.uifragments.WebFragment;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.BWCFlicBroadcastReceiver;

public class WebActivity extends AppCompatActivity {

    private FragmentTabHost mTabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("bodyweightconnect.splash",false)==false) {
            showSplashscreen();
        }

        ComponentName receiver = new ComponentName(getApplicationContext(), BWCFlicBroadcastReceiver.class);
        getPackageManager().setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        ActivityManager.getInstance().setCurrentActivity(this);

        //getSupportActionBar().hide();
        setContentView(R.layout.activity_web);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("meet").setIndicator("Meet"),
                MapFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("train").setIndicator("Train"),
                TrainContainerFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("stats").setIndicator("Stats"),
                StatleticsFragment.class, null);

        mTabHost.setCurrentTab(1);

    }

    private void switchToMainActivity(){
        Intent intent = new Intent(WebActivity.this, WebActivity.class);
        finish();
        startActivity(intent);
    }

    private void showPreferences(){
        Intent intent = new Intent(WebActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showSplashscreen() {
        Intent intent = new Intent(WebActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_web_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                showPreferences();
                return true;
            case R.id.action_back:
                switchToMainActivity();
                return true;
            case R.id.action_imprint:
                showWebSite(StaticURLs.IMPRINT);
                return true;
            case R.id.action_terms:
                showWebSite(StaticURLs.TERMS);
                return true;
            case R.id.action_privacy:
                showWebSite(StaticURLs.PRIVACY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showWebSite(StaticURLs url) {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(url.name());

        WebView wv = new WebView(this);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        System.out.println("URL:"+url.getUrl());
        wv.loadUrl(url.getUrl());
        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();


    }

//     Weiterleitungen in das Web-Fragment

    public void callClickAction(View view) {
        TrainContainerFragment tcf  = ((TrainContainerFragment) getSupportFragmentManager().findFragmentByTag("train"));
        if(tcf.getCurrentFragment() instanceof WebFragment) {
            ((WebFragment)tcf.getCurrentFragment()).callClickAction(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TrainContainerFragment tcf  = ((TrainContainerFragment) getSupportFragmentManager().findFragmentByTag("train"));
        if(tcf.getCurrentFragment() instanceof WebFragment) {
            ((WebFragment)tcf.getCurrentFragment()).onActivityResult(requestCode,resultCode,data);
        }
    }
}

package com.statletics.bodyweightconnect;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.statletics.bodyweightconnect.network.GetSettingsTask;
import com.statletics.bodyweightconnect.network.NetUtil;
import com.statletics.bodyweightconnect.type.PageType;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.MySpinnerPreference;
import com.statletics.bodyweightconnect.util.PageModel;
import com.statletics.bodyweightconnect.util.PagePreference;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by Tonni on 14.07.2016.
 */
public class SettingsActivity extends PreferenceActivity {

    private PreferenceScreen settingsPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceScreen settingsFree = (PreferenceScreen) getPreferenceScreen().findPreference("settings_metronom");
        addPreferencesFromResource(R.xml.settings_metronom, settingsFree);

        settingsPage = (PreferenceScreen) getPreferenceScreen().findPreference("settings_pages");
        addPages(settingsPage);

        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        bar.setTitleTextColor(Color.WHITE);
        bar.setSubtitleTextColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bar.getNavigationIcon().setTint(Color.WHITE);
        }

        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Preference statPred =(Preference)getPreferenceManager().findPreference("btn_getFromStatletics");
        statPred.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Data replace")
                        .setMessage("If you click ok, your metronom settings will be rewritten by statletics.com median values.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadDataFromStatletics(preference);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();

                return true;
            }
        });
    }

    private void addPages(PreferenceScreen settingsPages) {
        PageModel model = new PageModel();

        for(PageType pt :model.readPageTypes()){
            PagePreference p = new PagePreference(this, null);
            p.setPt(pt);
            settingsPages.addPreference(p);
        }
        //
        PagePreference p = new PagePreference(this, null);
        PageType pt = new PageType();
        pt.setName("Add new Page");
        pt.setUrl("https://");
        pt.setId(UUID.randomUUID().toString());
        p.setPt(pt);
        settingsPages.addPreference(p);
    }




    private void loadDataFromStatletics(Preference preference) {
        //Toast.makeText(getBaseContext(),"loadDataFromStatletics",Toast.LENGTH_LONG).show();
        String requestURL="https://statletics.com/ex_median.php";

        if(NetUtil.hasNetworkConnect(getApplicationContext())){
            try {
                Map<String,Float> data =new GetSettingsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, requestURL).get();
                for(String key: getPreferenceManager().getSharedPreferences().getAll().keySet()){
                    //Log.e("Settings","Key="+key);
                    if(key.startsWith("metronom_")) {
                        String a = key.replaceAll("metronom_","").replaceAll("_time_","").replaceAll(" ","").replaceAll("_","").replaceAll("-","");
                        a=a.toUpperCase().trim();
                        for(String keyData:data.keySet()){
                            String b = keyData.replaceAll(" ","").replaceAll("_","").replaceAll("-","").replace("HANDHELP","HH");
                            b = b.toUpperCase().trim();
                            if(a.equals(b)) {
                                SharedPreferences sp = getPreferenceManager().getSharedPreferences();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putFloat(key, data.get(keyData));
                                editor.apply();
                                editor.commit();
                                break;
                            }
                        }
                    }
                }

                //Repaint the screen
                PreferenceScreen settingsFree = (PreferenceScreen) getPreferenceScreen().findPreference("settings_metronom");
                for(int i=1; i<settingsFree.getRootAdapter().getCount();i++){
                    MySpinnerPreference msp = (MySpinnerPreference) settingsFree.getRootAdapter().getItem(i);
                    msp.setCurrentValue(getPreferenceManager().getSharedPreferences().getFloat(msp.getKey(),0));
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"No network connection",Toast.LENGTH_LONG).show();
        }
    }



    private void addPreferencesFromResource(int id, PreferenceGroup newParent) {
        PreferenceScreen screen = getPreferenceScreen();
        int last = screen.getPreferenceCount();
        addPreferencesFromResource(id);
        while (screen.getPreferenceCount() > last) {
            Preference p = screen.getPreference(last);
            screen.removePreference(p); // decreases the preference count
            newParent.addPreference(p);
        }

    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {
            setUpNestedScreen((PreferenceScreen) preference);
        }

        return false;
    }


    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        Toolbar bar;
        LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setTitle(preferenceScreen.getTitle());

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    public void refreshPages(){
        settingsPage.removeAll();
        addPages(settingsPage);
        ActivityManager.getInstance().getCurrentActivity().recreate();
    }

}

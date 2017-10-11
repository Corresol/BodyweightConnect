package com.statletics.bodyweightconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.statletics.bodyweightconnect.type.DataHolder;
import com.statletics.bodyweightconnect.type.DeviceType;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mainactivitylayout);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
    }

    public void callSetting(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }


    public void callWebView(View view) {

        RadioButton rbDeviceFlic = (RadioButton) findViewById(R.id.device_flic);
        RadioButton rbDeviceVoice = (RadioButton) findViewById(R.id.device_voice);
        RadioButton rbDeviceGamepad = (RadioButton) findViewById(R.id.device_gamepad);
        RadioButton rbDeviceWear = (RadioButton) findViewById(R.id.device_wear);

        DataHolder dh = new DataHolder();

        // Set URL ----------------------------------
//        if (rbSportsFreeletics.isChecked()) {
//            dh.setUrl("http://www.freeletics.com/");
//        } else if (rbSportsMadbarz.isChecked()) {
//            dh.setUrl("http://www.madbarz.com/");
//        } else {
//            dh.setUrl("http://www.google.de");
//        }

        // Set Device ----------------------------------
        if (rbDeviceFlic.isChecked()) {
            dh.setType(DeviceType.FLIC);
        } else if (rbDeviceVoice.isChecked()) {
            dh.setType(DeviceType.VOICE);
        } else if (rbDeviceGamepad.isChecked()) {
            dh.setType(DeviceType.GAMEPAD);
        } else if (rbDeviceWear.isChecked()) {
            dh.setType(DeviceType.WEAR);
        } else {
            dh.setType(DeviceType.OTHER);
        }

        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(Constants.DATA_FOR_INTENT, dh);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                callSetting(item.getActionView());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


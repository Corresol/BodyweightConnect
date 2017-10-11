package com.statletics.bodyweightconnect.uifragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.statletics.bodyweightconnect.Constants;
import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.type.DataHolder;
import com.statletics.bodyweightconnect.type.DeviceType;
import com.statletics.bodyweightconnect.type.PageType;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.PageModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Tonni on 20.09.2016.
 */
public class ChooseFragment extends Fragment {

    private final PageModel model = new PageModel();
    private RadioGroup pageGroup;
    private Map<RadioButton,PageType> map = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        final View view2 = inflater.inflate(R.layout.tab_choose_fragment,container,false);
        Button b = (Button)view2.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callWebView(view2);
            }
        });

        Button btAdd = (Button)view2.findViewById(R.id.btnAddPage);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPage(view2);
            }
        });

        pageGroup = (RadioGroup)view2.findViewById(R.id.sportsGroup);
        fillPageGroup();

        TextView order=(TextView) view2.findViewById(R.id.tvOrderFlic);
        order.setMovementMethod(LinkMovementMethod.getInstance());

        MobileAds.initialize(getContext().getApplicationContext(), "ca-app-pub-5171323585932839~5130260302");
        AdView mAdView = (AdView) view2.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        //AdRequest adRequest = new AdRequest.Builder().addTestDevice("CF5CE3BBB62645860EC912879027D617").build();
        mAdView.loadAd(adRequest);

        return view2;

    }

    private void fillPageGroup() {
        map.clear();
        pageGroup.removeAllViews();
        for(PageType pt:model.readPageTypes()){
            RadioButton bt = new RadioButton(this.getContext());
            bt.setText(pt.getName());
            map.put(bt,pt);
            pageGroup.addView(bt);
        }

    }


    public void callWebView(View view) {

        RadioButton rbDeviceFlic = (RadioButton) view.findViewById(R.id.device_flic);
        RadioButton rbDeviceVoice = (RadioButton) view.findViewById(R.id.device_voice);
        RadioButton rbDeviceGamepad = (RadioButton) view.findViewById(R.id.device_gamepad);
        RadioButton rbDeviceWear = (RadioButton) view.findViewById(R.id.device_wear);
        RadioButton rbDevicePhone = (RadioButton) view.findViewById(R.id.device_phone);

        DataHolder dh = new DataHolder();

        for(RadioButton rb:map.keySet()){
            if(rb.isChecked()){
                dh.setUrl(map.get(rb).getUrl());
            }
        }

        if(dh.getUrl()==null){
            Toast.makeText(view.getContext(),"Please select sports page first", Toast.LENGTH_LONG).show();
            return;
        }

        // Set Device ----------------------------------
        if (rbDeviceFlic.isChecked()) {
            dh.setType(DeviceType.FLIC);
        } else if (rbDeviceVoice.isChecked()) {
            dh.setType(DeviceType.VOICE);
        } else if (rbDeviceGamepad.isChecked()) {
            dh.setType(DeviceType.GAMEPAD);
        } else if (rbDeviceWear.isChecked()) {
            dh.setType(DeviceType.WEAR);
        } else if (rbDevicePhone.isChecked()) {
            dh.setType(DeviceType.PHONE);
        } else {
            dh.setType(DeviceType.OTHER);
        }


        WebFragment wf = new WebFragment();
        Bundle b = new Bundle();
        b.putSerializable(Constants.DATA_FOR_INTENT,dh);
        wf.setArguments(b);
        ((BaseContainerFragment)getParentFragment()).replaceFragment(wf,true);
    }


    public void addNewPage(View view){
        final View v =  ActivityManager.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.add_page,null);
        EditText url = (EditText) v.findViewById(R.id.inputUrl);
        url.setText("https://");
        new AlertDialog.Builder(view.getContext())
                .setTitle("add page")
                .setView(v)
                .setMessage("fill in name and url and press ok to add a page on the choose screen")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText name = (EditText) v.findViewById(R.id.inputName);
                        EditText url = (EditText) v.findViewById(R.id.inputUrl);

                        PageType pt = new PageType();
                        pt.setId(UUID.randomUUID().toString()).setName(name.getText().toString()).setUrl(url.getText().toString());
                        model.addPageType(pt);
                        fillPageGroup();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

}

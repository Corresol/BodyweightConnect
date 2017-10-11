package com.statletics.bodyweightconnect.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.SettingsActivity;
import com.statletics.bodyweightconnect.type.PageType;

import java.util.Collections;
import java.util.List;


/**
 * Created by Tonni on 16.11.2016.
 */

public class PagePreference  extends DialogPreference {

    private PageType pt;
    private TextView name;
    private TextView url;
    PageModel model = new PageModel();
    SettingsActivity sa;

    public PagePreference(Context context, AttributeSet attrs) {
        super(context,attrs);
        sa = (SettingsActivity)context;

        setDialogLayoutResource(R.layout.add_page);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
    }

    public PageType getPt() {
        return pt;
    }

    public void setPt(PageType pt) {
        this.pt = pt;
        if(pt!=null){
            setTitle(pt.getName());
            setSummary(pt.getUrl());
            if(!pt.getName().equals("Add new Page")) {
                setIcon(R.mipmap.ic_launcher);
            }else{
                setIcon(null);
            }
        }
        notifyChanged();
    }

    @Override
    protected View onCreateDialogView() {
        View v =  super.onCreateDialogView();

        name = (TextView)v.findViewById(R.id.inputName);
        url = (TextView)v.findViewById(R.id.inputUrl);

        if(!pt.getName().equals("Add new Page")) {
            name.setText(pt.getName());
        }
        url.setText(pt.getUrl());
        return v;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        if(which== Dialog.BUTTON_POSITIVE){
            getPt().setName(name.getText().toString());
            getPt().setUrl(url.getText().toString());
            setPt(getPt());

            List<PageType> pts =model.readPageTypes();
            boolean replace=false;
            for(PageType pt:pts){
                if(pt.getId().equals(getPt().getId())){
                    Collections.replaceAll(pts,pt,getPt());
                    replace=true;
                    model.storePageTypes(pts);
                    break;
                }
            }
            if(!replace){
                pts.add(getPt());
                model.storePageTypes(pts);
                sa.refreshPages();
            }
            notifyChanged();
        }
    }

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i== Dialog.BUTTON_NEUTRAL){
                    new android.support.v7.app.AlertDialog.Builder(getContext())
                            .setTitle("Delete Page")
                            .setMessage("Are you sure delete this page")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    model.removePageType(getPt());
                                    sa.refreshPages();
                                    notifyChanged();

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //nothing to do;
                                }
                            }).show();
                }
            }
        });
    }
}

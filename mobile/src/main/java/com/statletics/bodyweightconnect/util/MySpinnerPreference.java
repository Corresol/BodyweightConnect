package com.statletics.bodyweightconnect.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.statletics.bodyweightconnect.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tonni on 25.07.2016.
 */
public class MySpinnerPreference extends DialogPreference{

    private static final float DEFAULT_VALUE = 5.f;
    private float mCurrentValue;
    private View currentView;

    public MySpinnerPreference(Context context, AttributeSet attrs) {
        super(context,attrs);

        setDialogLayoutResource(R.layout.numberpicker_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        setDialogIcon(null);
    }

    private void initSpinner(View view) {
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner);
        List<Float> list = new ArrayList<>();
        list.add(0.0f);
        for(int i=4;i<600;i++){
            list.add((float)i/10f);
        }
        ArrayAdapter<Float> adp = new ArrayAdapter<Float>(getContext(), R.layout.spinneritem, list );
        spinner.setAdapter(adp);
        spinner.setSelection(adp.getPosition(mCurrentValue));

    }

    public void setCurrentValue(float value){
        mCurrentValue = value;
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        String a = String.valueOf(super.getSummary());
        a = a.replace("$1",mCurrentValue+" sec.");
        return a;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        System.out.println(">");
        if (positiveResult) {
            Spinner spinner = (Spinner)currentView.findViewById(R.id.spinner);
            System.out.println(">"+spinner.getSelectedItem());
            persistFloat((Float) spinner.getSelectedItem());
            mCurrentValue= (Float) spinner.getSelectedItem();
        }
        currentView = null;
        String a = (String) super.getSummary();
        setSummary("test");
        setSummary(a);

    }

    @Override
    protected View onCreateDialogView() {
        //System.out.println("2>");
        View v = super.onCreateDialogView();
        currentView = v;
        initSpinner(v);
        return v;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //System.out.println("1>");
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedFloat(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Float) defaultValue;
            persistFloat(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        //System.out.println("3>");
        return a.getFloat(index, DEFAULT_VALUE);
    }


}

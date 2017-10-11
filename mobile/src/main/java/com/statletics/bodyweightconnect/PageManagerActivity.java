package com.statletics.bodyweightconnect;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.statletics.bodyweightconnect.type.PageType;
import com.statletics.bodyweightconnect.util.ActivityManager;
import com.statletics.bodyweightconnect.util.PageListAdapter;
import com.statletics.bodyweightconnect.util.PageModel;

import java.util.UUID;

public class PageManagerActivity extends AppCompatActivity {


    private final PageModel model = new PageModel();
    private ListView liste = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        liste = (ListView)findViewById(R.id.pagelistList);


        final PageListAdapter adapter = new PageListAdapter(this, model.readPageTypes());
        liste.setAdapter(adapter);

        final Button addPage = (Button)findViewById(R.id.btnAddPage2);
        addPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPage(adapter);
            }
        });
    }

    private void addPage(final PageListAdapter adapter) {

        final View v =  ActivityManager.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.add_page,null);
        new AlertDialog.Builder(this)
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
                        adapter.refresh();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

    }

}

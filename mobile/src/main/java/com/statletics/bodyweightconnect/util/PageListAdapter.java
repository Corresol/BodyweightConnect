package com.statletics.bodyweightconnect.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.statletics.bodyweightconnect.R;
import com.statletics.bodyweightconnect.type.PageType;

import java.util.List;

/**
 * Created by Tonni on 13.11.2016.
 */

public class PageListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<PageType> mDataSource;
    private PageModel model = new PageModel();

    public PageListAdapter(Context context, List<PageType> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    //4
    @Override
    public View getView(final int position, View convertView,final ViewGroup parent) {
        // Get view for row item
        final View rowView = mInflater.inflate(R.layout.page_listview_item, parent, false);

        TextView name =
                (TextView) rowView.findViewById(R.id.pagelistitemname);

        TextView url =
                (TextView) rowView.findViewById(R.id.pagelistitemurl);

        final PageType pt = (PageType)getItem(position);
        name.setText(pt.getName());
        url.setText(pt.getUrl());

        ImageButton btnEdit = (ImageButton)rowView.findViewById(R.id.btnPageListItemEdit);
        ImageButton btnDel = (ImageButton)rowView.findViewById(R.id.btnPageListItemDelete);

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete Page")
                        .setMessage("Are you sure delete this page")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                model.removePageType(pt);
                                mDataSource.remove(pt);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing to do;
                            }
                        }).show();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v =  ActivityManager.getInstance().getCurrentActivity().getLayoutInflater().inflate(R.layout.add_page,null);
                final EditText name = (EditText) v.findViewById(R.id.inputName);
                final EditText url = (EditText) v.findViewById(R.id.inputUrl);
                name.setText(pt.getName());
                url.setText(pt.getUrl());

                new AlertDialog.Builder(view.getContext())
                        .setTitle("add page")
                        .setView(v)
                        .setMessage("fill in name and url and press ok to add a page on the choose screen")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PageType pt = (PageType) getItem(position);
                                pt.setName(name.getText().toString());
                                pt.setUrl(url.getText().toString());
                                model.storePageTypes(mDataSource);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        });

        return rowView;
    }

    public void refresh() {
        mDataSource=model.readPageTypes();
    }
}


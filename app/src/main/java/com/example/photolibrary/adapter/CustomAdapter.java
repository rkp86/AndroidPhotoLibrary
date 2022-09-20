package com.example.photolibrary.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.photolibrary.R;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> uris = new ArrayList<String>();
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, ArrayList<String> logos) {
        this.context = applicationContext;
        this.uris = logos;
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return uris.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.row_items, viewGroup, false);
        }
        ImageView icon = (ImageView) view.findViewById(R.id.image); // get the reference of ImageView
        icon.setImageURI(null);
        icon.setImageURI(Uri.parse(uris.get(i)));
        return icon;
    }
}
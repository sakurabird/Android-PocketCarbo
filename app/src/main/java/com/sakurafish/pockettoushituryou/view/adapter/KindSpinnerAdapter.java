package com.sakurafish.pockettoushituryou.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sakurafish.pockettoushituryou.R;
import com.sakurafish.pockettoushituryou.model.KindsData;

import java.util.ArrayList;

public class KindSpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<KindsData.Kinds> kindsList;

    public KindSpinnerAdapter(Context context) {
        super();
        this.context = context;
        kindsList = null;
    }

    public void setData(ArrayList<KindsData.Kinds> data) {
        kindsList = data;
    }

    @Override
    public int getCount() {
        return kindsList.size();
    }

    @Override
    public Object getItem(int position) {
        return kindsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.
                    inflate(R.layout.item_kind_spinner, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        KindsData.Kinds kinds = (KindsData.Kinds) getItem(position);
        tv.setText(kinds.name);

        return convertView;
    }

    @Override
    public View getDropDownView(int position,
                                View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_kind_spinner, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.name);
        KindsData.Kinds kinds = (KindsData.Kinds) getItem(position);
        tv.setText(kinds.name);
        tv.setTextColor(context.getResources().getColor(R.color.grey600));

        return convertView;
    }
}
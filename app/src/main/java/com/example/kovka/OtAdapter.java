package com.example.kovka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OtAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public OtAdapter(Context context, int listLayout, int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout = listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listViewItem = convertView;
        if (listViewItem == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewItem = inflater.inflate(listLayout, parent, false);
        }

        TextView nomer = listViewItem.findViewById(R.id.nomer);
        TextView zakaz = listViewItem.findViewById(R.id.zakaz);
        TextView time = listViewItem.findViewById(R.id.time);

        try {
            nomer.setText(usersList.get(position).getString("id"));
            // В таблице otchet поле называется "otchet", но если его нет — покажем пустую строку
            zakaz.setText(usersList.get(position).optString("otchet", ""));
            time.setText(usersList.get(position).getString("date"));
        } catch (JSONException je) {
            je.printStackTrace();
        }

        return listViewItem;
    }
}
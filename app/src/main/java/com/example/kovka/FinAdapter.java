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

public class FinAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public FinAdapter(Context context, int listLayout , int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout=listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView date = listViewItem.findViewById(R.id.date);
        TextView dohod = listViewItem.findViewById(R.id.dohod);
        TextView rashod = listViewItem.findViewById(R.id.rashod);
        TextView prib = listViewItem.findViewById(R.id.prib);
        try{
            date.setText(usersList.get(position).getString("date"));
            dohod.setText(usersList.get(position).getString("dohod"));
            rashod.setText(usersList.get(position).getString("rashod"));
            prib.setText(usersList.get(position).getString("prib"));
        }catch (JSONException je){
            je.printStackTrace();
        }
        return listViewItem;
    }
}


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

public class WorkingProcessAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public WorkingProcessAdapter(Context context, int listLayout , int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout=listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView id = listViewItem.findViewById(R.id.date);
        TextView class_work = listViewItem.findViewById(R.id.prof);
        TextView prof = listViewItem.findViewById(R.id.name);
        TextView name = listViewItem.findViewById(R.id.tz);
        TextView cod = listViewItem.findViewById(R.id.otchet);
        try{
            id.setText(usersList.get(position).getString("date"));
            class_work.setText(usersList.get(position).getString("prof"));
            prof.setText(usersList.get(position).getString("name"));
            name.setText(usersList.get(position).getString("tz"));
            cod.setText(usersList.get(position).getString("otchet"));
        }catch (JSONException je){
            je.printStackTrace();
        }
        return listViewItem;
    }
}

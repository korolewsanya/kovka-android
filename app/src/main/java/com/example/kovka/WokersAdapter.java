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

public class WokersAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public  WokersAdapter(Context context, int listLayout , int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout=listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView id = listViewItem.findViewById(R.id.nomer);
        TextView spec = listViewItem.findViewById(R.id.spec);
        TextView name = listViewItem.findViewById(R.id.name);
        try{
            id.setText(usersList.get(position).getString("id"));
            spec.setText(usersList.get(position).getString("spec"));
            name.setText(usersList.get(position).getString("name"));
        }catch (JSONException je){
            je.printStackTrace();
        }
        return listViewItem;
    }
}



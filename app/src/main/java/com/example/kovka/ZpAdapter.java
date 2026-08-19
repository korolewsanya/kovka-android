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

public class ZpAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public ZpAdapter(Context context, int listLayout , int field, ArrayList<JSONObject> usersList) {
        super(context, listLayout, field, usersList);
        this.context = context;
        this.listLayout=listLayout;
        this.usersList = usersList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listViewItem = inflater.inflate(listLayout, null, false);
        TextView id = listViewItem.findViewById(R.id.id);
        TextView date = listViewItem.findViewById(R.id.date);
        TextView spec = listViewItem.findViewById(R.id.spec);
        TextView name = listViewItem.findViewById(R.id.name);
        TextView nachis = listViewItem.findViewById(R.id.nachis);
        TextView poluch = listViewItem.findViewById(R.id.poluch);
        try{
            id.setText(usersList.get(position).getString("id"));
            date.setText(usersList.get(position).getString("date"));
            spec.setText(usersList.get(position).getString("spec"));
            name.setText(usersList.get(position).getString("name"));
            nachis.setText(usersList.get(position).getString("nachis"));
            poluch.setText(usersList.get(position).getString("poluch"));
        }catch (JSONException je){
            je.printStackTrace();
        }
        return listViewItem;
    }
}



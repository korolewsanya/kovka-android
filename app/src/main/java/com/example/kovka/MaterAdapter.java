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

public class MaterAdapter extends ArrayAdapter<JSONObject> {
    int listLayout;
    ArrayList<JSONObject> usersList;
    Context context;

    public MaterAdapter(Context context, int listLayout , int field, ArrayList<JSONObject> usersList) {
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
        TextView name = listViewItem.findViewById(R.id.name);
        TextView kup = listViewItem.findViewById(R.id.kup);
        TextView izras = listViewItem.findViewById(R.id.izras);
        TextView ost = listViewItem.findViewById(R.id.ost);
        TextView prise = listViewItem.findViewById(R.id.prise);
        TextView itogo = listViewItem.findViewById(R.id.itogo);
        try{
            id.setText(usersList.get(position).getString("id"));
            date.setText(usersList.get(position).getString("date"));
            name.setText(usersList.get(position).getString("name"));
            kup.setText(usersList.get(position).getString("kup"));
            izras.setText(usersList.get(position).getString("izras"));
            ost.setText(usersList.get(position).getString("ost"));
            prise.setText(usersList.get(position).getString("prise"));
            itogo.setText(usersList.get(position).getString("itogo"));
        }catch (JSONException je){
            je.printStackTrace();
        }
        return listViewItem;
    }
}


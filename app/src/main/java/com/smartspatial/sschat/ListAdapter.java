package com.smartspatial.sschat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {

    MainActivity main = new MainActivity();
    private ArrayList<ChatDTO> items;
    private Context context;
    LayoutInflater list_inflater;


    public ListAdapter(Context context, ArrayList<ChatDTO> items) {
        list_inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = list_inflater.inflate(R.layout.listview_content, null);
            holder.mUsername = (TextView) convertView.findViewById(R.id.itemname);
            holder.content = (TextView) convertView.findViewById(R.id.itemcontent);
            convertView.setTag(holder);
        }
        holder = (Holder) convertView.getTag();
        ChatDTO chatDTO = items.get(position);

        holder.mUsername.setText(chatDTO.getUserName());
        holder.content.setText(chatDTO.getMessage());

        return convertView;
    }


    class Holder {
        public TextView mUsername;
        public TextView content;
    }
}

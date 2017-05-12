package com.remon.ListViewClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.remon.R;

import java.util.ArrayList;

/**
 * Created by lg on 2017-05-13.
 */

public class ListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    private ArrayList<ListViewItem> displayList = new ArrayList<ListViewItem>();

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return displayList.size() ;
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();

        //"listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_form, parent, false);
        }
        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView nameTextView = (TextView) convertView.findViewById(R.id.textViewName) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.textViewDescription) ;
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = displayList.get(position);
        // 아이템 내 각 위젯에 데이터 반영
        nameTextView.setText(listViewItem.getName());
        descTextView.setText(listViewItem.getDescription());
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return displayList.get(position) ;
    }
    public void addItem(String name, String desc, Marker marker)
    {
        ListViewItem item = new ListViewItem();
        item.setName(name);
        item.setDesc(desc);
        item.setMarker(marker);
        listViewItemList.add(item);
        displayList.add(item);
    }

    public void fillter(String searchText)
    {
        displayList.clear();
        if (searchText.length() == 0)
        {
            displayList.addAll(listViewItemList);
        }
        else
        {
            int size = listViewItemList.size();
            for (int i = 0; i < size; i++)
            {
                String item = listViewItemList.get(i).getName();
                if (item.contains(searchText))
                {
                    displayList.add(listViewItemList.get(i));
                }
            }
        }
        this.notifyDataSetChanged();
    }
}

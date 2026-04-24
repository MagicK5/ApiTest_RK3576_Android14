package com.example.zjfb_android14;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fbsdk.FbSDK;

import java.util.List;

// 创建一个简单的 ViewHolder 模式 Adapter
public class WifiListAdapter extends ArrayAdapter<FbSDK.QyScanResultInfo> {
    public WifiListAdapter(Context context, List<FbSDK.QyScanResultInfo> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        FbSDK.QyScanResultInfo item = getItem(position);
        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        text1.setText(item.ssid);
        text2.setText("强度: " + item.level + " | 频率: " + item.frequency);
        return convertView;
    }
}

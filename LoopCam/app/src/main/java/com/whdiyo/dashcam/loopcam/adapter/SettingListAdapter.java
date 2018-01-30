package com.whdiyo.dashcam.loopcam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.whdiyo.dashcam.loopcam.LoopCamApplication;
import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.activity.SettingsActivity;
import com.whdiyo.dashcam.loopcam.setting.AppSetting;
import com.whdiyo.dashcam.loopcam.util.Const;

/**
 * Created by Wang on 1/28/2018
 */

public class SettingListAdapter extends BaseAdapter {

    private Context context;
    private String[] stringList;
    private String[] valueList;

    private static LayoutInflater inflater=null;
    public SettingListAdapter(SettingsActivity activity, String[] listItemList) {
        // TODO Auto-generated constructor stub
        stringList = listItemList;
        if (stringList.length > 0) {
            valueList = new String[stringList.length];
        }
        context = activity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return stringList.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.listitem_setting, null);
        holder.txtItemName = rowView.findViewById(R.id.txt_name);
        holder.txtItemValue = rowView.findViewById(R.id.txt_value);
        holder.txtItemName.setText(stringList[position]);
        holder.txtItemValue.setText(valueList[position]);

        AppSetting setting = ((LoopCamApplication)LoopCamApplication.getApplication()).appSetting;
        switch (position) {
            case 0:
            {
                if (setting.getSpeedUnit() == Const.METER_PER_HOUR)
                    holder.txtItemValue.setText("(MPH)");
                else
                    holder.txtItemValue.setText("(KPH)");
            }
                break;
            case 1:
                holder.txtItemValue.setText(String.valueOf(setting.getLoopSeconds()));
                break;
            case 2:
            {
                if (setting.isAutoRecord())
                    holder.txtItemValue.setText("YES");
                else
                    holder.txtItemValue.setText("NO");
            }
                break;
            case 3:
            {
                if (setting.getVideoSetting() == Const.VIDEO_SETTING_LOW)
                    holder.txtItemValue.setText("Low");
                else if (setting.getVideoSetting() == Const.VIDEO_SETTING_MEDIUM)
                    holder.txtItemValue.setText("Medium");
                else if (setting.getVideoSetting() == Const.VIDEO_SETTING_HIGH)
                    holder.txtItemValue.setText("High");
            }
                break;
        }

        return rowView;
    }

    public class Holder {
        TextView txtItemName;
        TextView txtItemValue;
    }


}

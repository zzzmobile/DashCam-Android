package com.whdiyo.dashcam.loopcam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.whdiyo.dashcam.loopcam.R;
import com.whdiyo.dashcam.loopcam.activity.VideoListActivity;
import com.whdiyo.dashcam.loopcam.util.Utils;

import java.util.ArrayList;

/**
 * Created by Wang on 1/29/2018
 */

public class VideoListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VideoListItem> videoList;

    private LayoutInflater inflater=null;

    public VideoListAdapter(VideoListActivity activity, ArrayList<VideoListItem> listItemList) {
        // TODO Auto-generated constructor stub
        videoList = listItemList;
        context = activity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return videoList.size();
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
        rowView = inflater.inflate(R.layout.listitem_video, null);
        holder.imgVideoThumb = rowView.findViewById(R.id.img_video);
        holder.txtVideoName = rowView.findViewById(R.id.txt_video_name);
        holder.txtVideoDuration = rowView.findViewById(R.id.txt_video_duration);
        holder.txtVideoSize = rowView.findViewById(R.id.txt_video_size);

        VideoListItem videoItem = videoList.get(position);
        holder.imgVideoThumb.setImageBitmap(videoItem.getThumbImage());
        holder.txtVideoName.setText(videoItem.getFileName());
        holder.txtVideoDuration.setText(Utils.getTimerString(videoItem.getTimeStamp()));
        holder.txtVideoSize.setText(Utils.getStorageString(videoItem.getFileSize()));

        return rowView;
    }

    public class Holder {
        ImageView imgVideoThumb;
        TextView txtVideoName;
        TextView txtVideoDuration;
        TextView txtVideoSize;
    }

}

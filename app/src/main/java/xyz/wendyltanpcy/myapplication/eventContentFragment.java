package xyz.wendyltanpcy.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Wendy on 2017/9/6.
 */

public class eventContentFragment extends Fragment {

    private View mView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events_content_frag,container,false);
        return mView;
    }

    public void refresh(String newsTitle, String newsContent,String eventDeadLine){
        TextView newsTitleText = mView.findViewById(R.id.event_name);
        TextView newsContentText =  mView.findViewById(R.id.event_detail);
        TextView eventDeadLineText = mView.findViewById(R.id.event_deadline);
        newsTitleText.setText(newsTitle);
        newsContentText.setText(newsContent);
        eventDeadLineText.setText(eventDeadLine);
    }
}

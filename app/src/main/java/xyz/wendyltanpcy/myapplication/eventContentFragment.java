package xyz.wendyltanpcy.myapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Wendy on 2017/9/6.
 */

public class eventContentFragment extends Fragment {

    private View mView;


    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events_content_frag,container,false);

        eventNameText = mView.findViewById(R.id.event_name);
        eventDetailText =  mView.findViewById(R.id.event_detail);
        eventDeadLineText = mView.findViewById(R.id.event_deadline);

        return mView;
    }

    public void refresh(TodoEvent event){
        eventNameText.setText(event.getEventName());
        eventDetailText.setText(event.getEventDetail());
        eventDeadLineText.setText(event.getEventDeadLine());
    }
}

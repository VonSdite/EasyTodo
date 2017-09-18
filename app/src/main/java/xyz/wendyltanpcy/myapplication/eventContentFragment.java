package xyz.wendyltanpcy.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Wendy on 2017/9/6.
 */

public class eventContentFragment extends Fragment {

    private View mView;
    private ImageView chooseDate;
    private ImageView chooseAlarm;
    private String date_return;
    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events_content_frag,container,false);
        chooseDate = mView.findViewById(R.id.choose_date);
        chooseAlarm = mView.findViewById(R.id.choose_alarm);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),PickDateActvity.class);
                startActivityForResult(i,1);
            }
        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK) {
                    date_return = data.getStringExtra("date_return");
                    eventDeadLineText.setText(date_return);
                }

        }
    }

    public void refresh(String eventName, String eventDetail,String eventDeadLine){
        eventNameText = mView.findViewById(R.id.event_name);
        eventDetailText =  mView.findViewById(R.id.event_detail);
        eventDeadLineText = mView.findViewById(R.id.event_deadline);
        eventNameText.setText(eventName);
        eventDetailText.setText(eventDetail);
        eventDeadLineText.setText(eventDeadLine);
    }
}

package xyz.wendyltanpcy.easytodo.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.easytodo.R;

/**
 * 设置时间
 */
public class PickTimeFragment extends DialogFragment {


    private TimePicker mTimePicker;


    public static final String EXTRA_TIME = "xyz.wendyltanpcy.easytodo.TodoList.time";

    public static final String ARG_TIME = "time";


    public static PickTimeFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,date);

        PickTimeFragment fragment = new PickTimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = "PickTimeFragment";
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_TIME);//得到的date参数用calendar处理，初始化

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.pick_time_fragment, null);

        mTimePicker = v.findViewById(R.id.timePicker);

        mTimePicker.setIs24HourView(true);

        return new AlertDialog.Builder(getActivity()) //fluent interface
                .setView(v)
                .setTitle("选择时间")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int hour = mTimePicker.getHour();
                                int min = mTimePicker.getMinute();
                                Intent intent = new Intent();
                                intent.putExtra("hour", hour);
                                intent.putExtra("min", min);
                                sendResult(Activity.RESULT_OK, intent);

                            }//点击的时候使用sendResult回传数据。
                        })
                .create();
    }

    private void sendResult(int resultCode, Intent intent){
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}


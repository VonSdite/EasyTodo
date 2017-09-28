package xyz.wendyltanpcy.myapplication.TodoList;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;


import xyz.wendyltanpcy.myapplication.R;


/**
 * Created by Wendy on 2017/9/17.
 */

public class PickDateFragment extends Fragment {

    private FloatingActionButton saveButton;
    private String date;
    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePicker mDatePicker;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pick_date_fragment,container,false);


        saveButton = view.findViewById(R.id.saveDate);
        mDatePicker = view.findViewById(R.id.datePicker);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"you save the date!",Toast.LENGTH_SHORT).show();
                Intent i = getActivity().getIntent();
                i.putExtra("date_return",date);
                getActivity().setResult(Activity.RESULT_OK,i);
                getActivity().finish();
            }
        });


        mDatePicker.init( mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(),new  DatePicker.OnDateChangedListener() {
            @Override

            public void onDateChanged(DatePicker view, int year, int monthOfYear, int  dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear+1;
                mDay = dayOfMonth;
                date = new String(new StringBuilder().append("在 ").append(mYear)
                        .append("年").append(mMonth).append("月")// 得到的月份+1，因为从0开始
                        .append(mDay).append("日").append(" 前完成"));

            }

        });
        return view;
    }
}

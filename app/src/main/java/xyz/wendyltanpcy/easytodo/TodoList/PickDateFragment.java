package xyz.wendyltanpcy.easytodo.TodoList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import xyz.wendyltanpcy.easytodo.R;


/**
 * 设置日期
 */

public class PickDateFragment extends DialogFragment {


    private DatePicker mDatePicker;

    public static final String EXTRA_DATE = "xyz.wendyltanpcy.myapplication.TodoList.date";
    private static final String ARG_DATE = "date";

    public static PickDateFragment newInstance(Date date){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE,date);

        PickDateFragment fragment = new PickDateFragment();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        Date date = (Date)getArguments().getSerializable(ARG_DATE);//得到的date参数用calendar处理，初始化

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.pick_date_fragment,null);

        mDatePicker = v.findViewById(R.id.datePicker);
        mDatePicker.init(year,month,day,null);

        return new AlertDialog.Builder(getActivity()) //fluent interface
                .setView(v)
                .setTitle("选择日期")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year,month,day).getTime();

                                Intent intent =  new Intent();
                                intent.putExtra(EXTRA_DATE,date);
                                sendResult(Activity.RESULT_OK,date);

                            }//点击的时候使用sendResult回传数据。
                        })
                .create();
    }

    private void sendResult(int resultCode,Date date){

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE,date);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


}

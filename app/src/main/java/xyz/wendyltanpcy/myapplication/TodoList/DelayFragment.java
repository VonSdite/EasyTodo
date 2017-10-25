package xyz.wendyltanpcy.myapplication.TodoList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.myapplication.R;

/**
 * Created by Wendy on 2017/10/20.
 */

public class DelayFragment extends DialogFragment {


    private  List<Map<String, Object>> infoList;

    public static DelayFragment newInstance(List<Map<String, Object>> list){

        Bundle args = new Bundle();
        args.putSerializable("infoList", (Serializable) list);
        DelayFragment fragment = new DelayFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        infoList = (List<Map<String, Object>>) getArguments().getSerializable("infoList");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.delay_list_fragment,null);

        ListView delayView = v.findViewById(R.id.delay_list);


        final SimpleAdapter adapter = new SimpleAdapter(getActivity(), infoList, R.layout.delay_list_item,
                new String[]{"title", "date","delay"},
                new int[]{R.id.delay_event_title, R.id.delay_event_old_date,R.id.delay_check});

        final List<Integer> callBackList = new ArrayList<>();

        delayView.setAdapter(adapter);
        delayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                ListView v = (ListView) adapterView;
                CheckBox btn = (CheckBox) view.findViewById(R.id.delay_check);
                if (btn.isChecked()) {
                    btn.setChecked(false);
                    callBackList.remove(callBackList.size()-1);
                } else {
                    btn.setChecked(true);
                    Integer a = new Integer(position);
                    callBackList.add(a);
                }






            }
        });

        return new AlertDialog.Builder(getActivity()) //fluent interface
                .setView(v)
                .setTitle("选择要延迟的事件")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MainActivity.getDelayPosList(callBackList);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                })

                .create();
    }






}

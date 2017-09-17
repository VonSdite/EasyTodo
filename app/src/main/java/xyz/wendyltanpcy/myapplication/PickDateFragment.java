package xyz.wendyltanpcy.myapplication;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;


import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * Created by Wendy on 2017/9/17.
 */

public class PickDateFragment extends Fragment{

    private FloatingActionButton saveButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pick_date_fragment,container,false);
        saveButton = view.findViewById(R.id.saveDate);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"you save the date!",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });
        return view;
    }
}

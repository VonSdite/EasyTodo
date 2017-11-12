package xyz.wendyltanpcy.easytodo.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.wendyltanpcy.easytodo.R;

/**
 * Created by Wendy on 2017/9/6.
 */

public class EventListFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list_frag,container,false);
        return  view;
    }

}

package xyz.wendyltanpcy.myapplication.FinishList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.wendyltanpcy.myapplication.R;

/**
 * Created by Wendy on 2017/9/28.
 */

public class FinishEventListFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.finish_event_list_frag,container,false);
        return  view;
    }
}

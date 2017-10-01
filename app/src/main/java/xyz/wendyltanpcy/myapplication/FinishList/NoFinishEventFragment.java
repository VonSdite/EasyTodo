package xyz.wendyltanpcy.myapplication.FinishList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.wendyltanpcy.myapplication.R;

/**
 * Created by Wendy on 2017/10/1.
 */

public class NoFinishEventFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.no_finish_frag,container,false);
        return v;
    }
}

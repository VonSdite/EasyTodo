package xyz.wendyltanpcy.myapplication.TodoBrowser;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.wendyltanpcy.myapplication.R;

/**
 * Created by Wendy on 2017/10/10.
 */

public class PageListFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_list_frag,container,false);
        return  view;
    }
}

package xyz.wendyltanpcy.myapplication.TodoList;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.R.id.list;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Wendy on 2017/9/6.
 */

public class EventListFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list_frag,container,false);
        return  view;
    }

}

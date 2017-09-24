package xyz.wendyltanpcy.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by Wendy on 2017/9/16.
 */

public class PickDateActvity extends AppCompatActivity{



    private PickDateFragment mPickDateFragment;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_date_activity);

        FragmentManager fm = getSupportFragmentManager();
        mPickDateFragment = (PickDateFragment)fm.findFragmentById(R.id.pd_fragment);

        if(mPickDateFragment == null )
        {
            mPickDateFragment = new PickDateFragment();
            fm.beginTransaction().add(R.id.pd_activity,mPickDateFragment).commit();
        }

    }
}

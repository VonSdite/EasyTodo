package xyz.wendyltanpcy.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import xyz.wendyltanpcy.myapplication.model.TodoEvent;

public class eventContentActivity extends AppCompatActivity {


    public static void actionStart(Context context, String eventName, String eventDetail,String eventDeadLine){
        Intent intent = new Intent(context,eventContentActivity.class);
        intent.putExtra("event_name",eventName);
        intent.putExtra("event_detail",eventDetail);
        intent.putExtra("event_dead_line",eventDeadLine);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_content);
        if(getSupportActionBar() != null){
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String eventName = getIntent().getStringExtra("event_name");
        String eventDetail = getIntent().getStringExtra("event_detail");
        String eventDeadLine = getIntent().getStringExtra("event_dead_line");

        eventContentFragment eventContentFragment = (eventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        eventContentFragment.refresh(eventName,eventDetail,eventDeadLine);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

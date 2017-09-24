package xyz.wendyltanpcy.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

public class eventContentActivity extends AppCompatActivity {

    private String date_return;
    private FloatingActionButton saveDetailButton;
    private ImageView chooseDate;
    private ImageView chooseAlarm;
    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    private static long position;
    private static TodoEvent Event;
    private static EventsAdapter.ViewHolder holder;

    public static void actionStart(Context context, TodoEvent event,EventsAdapter.ViewHolder hd){
        Intent intent = new Intent(context,eventContentActivity.class);
        position = event.getId()-1;
        Event = event;
        holder = hd;
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


        eventContentFragment eventContentFragment = (eventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        eventContentFragment.refresh(Event);

        chooseDate = (ImageView) findViewById(R.id.choose_date);
        chooseAlarm = (ImageView) findViewById(R.id.choose_alarm);
        eventNameText = (TextView) findViewById(R.id.event_name);
        eventDetailText = (TextView) findViewById(R.id.event_detail);
        eventDeadLineText = (TextView) findViewById(R.id.event_deadline);


        saveDetailButton = (FloatingActionButton) findViewById(R.id.save_detail_button);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(eventContentActivity.this,PickDateActvity.class);
                startActivityForResult(i,1);
            }
        });
        saveDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event.setEventName(eventNameText.getText().toString());
                Event.setEventDetail(eventDetailText.getText().toString());
                Event.setEventDeadLine(eventDeadLineText.getText().toString());
                Event.save();
                holder.notify();
                Toast.makeText(getApplicationContext(),"Detail save!",Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK) {
                    date_return = data.getStringExtra("date_return");
                    eventDeadLineText.setText(date_return);
                }

        }
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

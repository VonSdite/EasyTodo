package xyz.wendyltanpcy.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class eventContentActivity extends AppCompatActivity {

    public static void actionStart(Context context, String eventName, String eventDetail){
        Intent intent = new Intent(context,eventContentActivity.class);
        intent.putExtra("event_name",eventName);
        intent.putExtra("event_detail",eventDetail);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_content);
        String eventName = getIntent().getStringExtra("event_name");
        String eventDetail = getIntent().getStringExtra("event_detail");

        eventContentFragment eventContentFragment = (eventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        eventContentFragment.refresh(eventName,eventDetail);

    }


}

package xyz.wendyltanpcy.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
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
    private static TodoEvent Event;
    private ImageView eventImage;
    private static EventsAdapter.ViewHolder holder;
    private static final int SUCCESSCODE = 100;
    private String mPublicPhotoPath;
    private static final int REQ_GALLERY = 333;
    private static final int REQUEST_CODE_PICK_IMAGE = 222;


    public static void actionStart(Context context, TodoEvent event,EventsAdapter.ViewHolder hd){
        Intent intent = new Intent(context,eventContentActivity.class);
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
        eventImage = (ImageView) findViewById(R.id.event_content_image);

        if (Event.getEventImageBitMap()!=null){
            byte[]images=Event.getEventImageBitMap();
            Bitmap bitmap = BitmapFactory.decodeByteArray(images,0,images.length);
            eventImage.setImageBitmap(bitmap);
        }


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

    //拍照的功能
    private void showTakePicture() {
        PermissionGen.with(eventContentActivity.this)
                .addRequestCode(SUCCESSCODE)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //权限申请成功
    @PermissionSuccess(requestCode = SUCCESSCODE)
    public void doSomething() {
        //申请成功
        startTake();
    }

    @PermissionFail(requestCode = SUCCESSCODE)
    public void doFailSomething() {
        Toast.makeText(eventContentActivity.this,"Ask for permission failed!",Toast.LENGTH_SHORT).show();
    }

    private byte[]img(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    private void startTake() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断是否有相机应用
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //创建临时图片文件
            File photoFile = null;
            try {
                photoFile = PictureUtils.createPublicImageFile();
                mPublicPhotoPath = photoFile.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //设置Action为拍照
            if (photoFile != null) {
                takePictureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //这里加入flag
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri photoURI = FileProvider.getUriForFile(this, "applicationId.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQ_GALLERY);
            }
        }
    }

    private Uri uri;
    String path;
    int mTargetW;
    int mTargetH;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTargetW = eventImage.getWidth();
        mTargetH = eventImage.getHeight();

        switch (requestCode) {
            case 1:
                if (resultCode==RESULT_OK) {
                    date_return = data.getStringExtra("date_return");
                    eventDeadLineText.setText(date_return);
                }
            //拍照
            case REQ_GALLERY:
                if (resultCode != Activity.RESULT_OK) return;
                uri = Uri.parse(mPublicPhotoPath);
                path = uri.getPath();
                PictureUtils.galleryAddPic(mPublicPhotoPath, this);
                break;
            //获取相册的图片
            case REQUEST_CODE_PICK_IMAGE:
                if (data == null) return;
                uri = data.getData();
                int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
                if (sdkVersion >= 19) {
                    path = this.uri.getPath();
                    path = PictureUtils.getPath_above19(eventContentActivity.this, this.uri);
                } else {
                    path = PictureUtils.getFilePath_below19(eventContentActivity.this, this.uri);
                }
                break;
        }
        Bitmap imageBitmap = PictureUtils.getSmallBitmap(path, mTargetW, mTargetH);
        eventImage.setImageBitmap(imageBitmap);
        byte[] images = img(imageBitmap);
        Event.setEventImageBitMap(images);
        Event.save();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.captureImage) {
           Toast.makeText(this,"capturing!",Toast.LENGTH_SHORT).show();
            showTakePicture();
        }

        return super.onOptionsItemSelected(item);
    }


}

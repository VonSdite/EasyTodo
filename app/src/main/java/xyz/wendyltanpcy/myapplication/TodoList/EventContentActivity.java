package xyz.wendyltanpcy.myapplication.TodoList;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.helper.PictureUtils;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

public class EventContentActivity extends AppCompatActivity {

    private String dateReturn;
    private FloatingActionButton saveDetailButton;
    private ImageView chooseDate;
    private ImageView chooseAlarm;
    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    private static TodoEvent Event;
    private ImageView eventImage;
    private EventsAdapter.ViewHolder holder;

    //deal with pics
    private static final int SUCCESSCODE = 100;
    private String mPublicPhotoPath;
    private static final int REQ_GALLERY = 333;
    private static final int REQUEST_CODE_PICK_IMAGE = 222;


    public void actionStart(Context context, TodoEvent event){
        Intent intent = new Intent(context,EventContentActivity.class);
        Event = event;
        context.startActivity(intent);
    }

    /*
    默认的构造器
     */
    public EventContentActivity(){

    }

    /*
    构造的时候传入适配器
     */
    public EventContentActivity(EventsAdapter.ViewHolder hd){
        holder = hd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        if(getSupportActionBar() != null){
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(Event.getEventName());
        Glide.with(this);

        EventContentFragment EventContentFragment = (EventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        EventContentFragment.refresh(Event);



        chooseDate = (ImageView) findViewById(R.id.choose_date);
        chooseAlarm = (ImageView) findViewById(R.id.choose_alarm);
        eventNameText = (TextView) findViewById(R.id.event_name);
        eventDetailText = (TextView) findViewById(R.id.event_detail);
        eventDeadLineText = (TextView) findViewById(R.id.event_deadline);
        eventImage = (ImageView) findViewById(R.id.event_content_image);
        saveDetailButton = (FloatingActionButton) findViewById(R.id.save_detail_button);

        /*
        如果事件具有图片的字节属性，就设置事件图片
         */
        if (Event.getEventImageBitMap()!=null){
            byte[]images=Event.getEventImageBitMap();
            Bitmap bitmap = BitmapFactory.decodeByteArray(images,0,images.length);
            eventImage.setImageBitmap(bitmap);
        }



        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventContentActivity.this,PickDateActvity.class);
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

    /*
    拍照的功能
     */
    private void showTakePicture() {
        PermissionGen.with(EventContentActivity.this)
                .addRequestCode(SUCCESSCODE)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    /*
    使用PermissionGen处理权限
     */
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
        Toast.makeText(EventContentActivity.this,"Ask for permission failed!",Toast.LENGTH_SHORT).show();
    }

    /*
    将位图转换成字节数组
     */
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
                    dateReturn = data.getStringExtra("dateReturn");
                    eventDeadLineText.setText(dateReturn);
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
                    path = PictureUtils.getPath_above19(EventContentActivity.this, this.uri);
                } else {
                    path = PictureUtils.getFilePath_below19(EventContentActivity.this, this.uri);
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
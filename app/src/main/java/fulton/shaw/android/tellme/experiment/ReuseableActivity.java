package fulton.shaw.android.tellme.experiment;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.thread.SerialActionsPreferNewest;
import fulton.util.android.utils.Util;

public class ReuseableActivity extends AppCompatActivity {
//  For auto complete
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.calendar_subitem);
//        setContentView(R.layout.auto_complete_layout);
//
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
//                getResources().getStringArray(R.array.auto_complete)
//        );
//        AutoCompleteTextView autoView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
//        autoView.setAdapter(adapter);
//
//
//    }

//    @Override
//    protected  void onCreate(Bundle savedInstance)
//    {
//        super.onCreate(savedInstance);
//        setContentView(R.layout.login_activity);
//        //testSendBroadcast();
//        playMusic();
//
//
//
////        while(true)
////        {
////            //Log.v("Tag","Message");
////            System.out.println("By printf");
////            try {
////                Thread.sleep(1000*4);
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            }
////        }
//
//    }
    private long mLastRatioUpdateTime=0;
    public static final long UPDATE_MIN_TIME_DELTA=1000;
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.canvas_view);

        final MyView myView = (MyView) findViewById(R.id.myview);
        final ImageView imageView = (ImageView) findViewById(R.id.imageview);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        final TextView mRatio=(TextView)findViewById(R.id.ratio);
        setSupportActionBar(toolbar);

        Util.logi("MyView", "is null" + (imageView == null), 1, 1);

        Button saveBtn = (Button) findViewById(R.id.saveButton);
        Button clearBtn = (Button) findViewById(R.id.clearButton);
        Button testBtn = (Button) findViewById(R.id.testButton);

        final SerialActionsPreferNewest ratioUpdater=new SerialActionsPreferNewest();
        ratioUpdater.start();

        myView.setDrawnRatioChangedListener(new MyView.DrawnRatioChangedListener() {

            @Override
            public void onDrawnRatioChanged(View v, final int condition) {
                final MyView myview=(MyView)v;
                ratioUpdater.putAction(new Runnable() {
                    @Override
                    public void run() {
                        switch (condition) {
                            case MyView.CONDITION_RESET:
                                ReuseableActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRatio.setText("0.0%");
                                    }
                                });
                                mLastRatioUpdateTime=System.currentTimeMillis();
                                break;
                            case MyView.CONDITION_MOVE:
                                long curTime=System.currentTimeMillis();
                                if(curTime - mLastRatioUpdateTime >= UPDATE_MIN_TIME_DELTA)
                                {
                                    final float ratio = myview.calculateDrawnPercentage();
                                    ReuseableActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRatio.setText("" + (ratio * 100) + "%");
                                        }
                                    });
                                    mLastRatioUpdateTime=curTime;
                                }
                                break;
                            case MyView.CONDITION_UP:
                                final float ratio = myview.calculateDrawnPercentage();
                                ReuseableActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRatio.setText("" + (ratio * 100) + "%");
                                    }
                                });
                                mLastRatioUpdateTime=System.currentTimeMillis();
                                break;
                        }
                    }
                });
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyView saveView = myView;
                Bitmap bmap = saveView.getDrawingCache();
//                String path= Environment.getExternalStorageDirectory().getAbsolutePath();
//                File file=new File(path+"/image.png");

                        ;

                imageView.setImageBitmap(bmap.copy(bmap.getConfig(),true));
                imageView.invalidate();

                FileOutputStream ostream;
                try {
//                    file.createNewFile();
//                    ostream=new FileOutputStream(file);
                    ostream = openFileOutput("image.png", MODE_WORLD_READABLE);
                    bmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.flush();
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myView.clearDrawings();
            }
        });

//        testBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent phoneIntent = new Intent(Intent.ACTION_CALL); // 或者ACTION_DIAL
//                phoneIntent.setData(Uri.parse("tel:911"));
//                if (ActivityCompat.checkSelfPermission(ReuseableActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    Util.logi("No Permission");
//                    return;
//                }
//                Util.logi("Has Permission");
//                startActivity(phoneIntent);
//            }
//        });

        sendNotification();

    }

    public void onClickTestButton(View v)
    {
        //1. //SerialActionsPreferNewest.HowToUse();
//        SerialActionsPreferNewest.HowToUse();

        //2. sqlite test





    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_activities, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent=null;
        switch (item.getItemId())
        {
            case R.id.action0:
                //insert your code here
                intent=new Intent(this,CrossFadingActivity.class);
                startActivity(intent);
                break;
            case R.id.action1:
                intent=new Intent(this,ContentProviderActivity.class);
                startActivity(intent);
                break;
            case R.id.action2:
                intent=new Intent(this,MessageHandlerActivity.class);
                startActivity(intent);
                break;
            case R.id.action3:
                intent=new Intent(this,CalendarViewActivity.class);
                startActivity(intent);
                break;
            case R.id.action4:
                intent=new Intent(this,TestViewActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    protected void testSendBroadcast()
    {
        Intent intent=new Intent();
        intent.setAction("fulton.show.android.maketoast");
        sendBroadcast(intent);
    }

    protected  void playMusic()
    {
        AssetManager am=getAssets();
        MediaPlayer mp=new MediaPlayer();
        try {
            AssetFileDescriptor afd=am.openFd("Musiz_By_Like_You.mp3");
            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendNotification()
    {
        Intent intentEcho = new Intent(this,WeChatActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(WeChatActivity.class);
        stackBuilder.addNextIntent(intentEcho);
        PendingIntent pendingIntent=stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.icon);
        builder.setContentTitle("Alert Title");
        builder.setContentText("Hello,we send a notification.(If you click,EchoIntentService will be started)");
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(10,builder.build());
    }

}

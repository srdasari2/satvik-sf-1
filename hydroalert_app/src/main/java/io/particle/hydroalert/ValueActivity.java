package io.particle.hydroalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class ValueActivity extends AppCompatActivity {

    public static final String ARG_VALUE = "ARG_VALUE";
    public static final String ARG_DEVICEID = "ARG_DEVICEID";
    public static final String DEVICE_LIST ="DEVICE_LIST";
    private Handler mHandler;
    private String email;
    private String password;
    private Button refreshButton;
    private TextView tv;
    int bridgeLevel = 10;    //BRIDGE LEVEL
    private TextView alertTitle;
    private ImageView warningImage;
    RelativeLayout mRelativeLayout;
    ParticleDevice mDevice;
    private ArrayList<ParticleDevice> devices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);
        this.mHandler = new Handler();
        mDevice = getIntent().getParcelableExtra("device");
        int value = getIntent().getIntExtra(ARG_VALUE, 0);
        devices = getIntent().getParcelableArrayListExtra(DEVICE_LIST);
        processValues(value);
        mRunnable.run();
        refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
                // Do network work on background thread
                Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
                    @Override
                    public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                        //ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));

                        int variable;
                        try {
                            variable = mDevice.getIntVariable("in");
                            if (variable < 100) {
                                // SmsManager smsManager = SmsManager.getDefault();
                                // smsManager.sendTextMessage("5123836659", null, "Flood Alert", null, null);

                            }
                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            //Toaster.l(ValueActivity.this, e.getMessage());
                            variable = -1;
                        }
                        return variable;
                    }

                    @Override
                    public void onSuccess(Object i) { // this goes on the main thread
                        int iWaterLevel = ((Integer) i).intValue();
                        processValues(iWaterLevel);
                    }

                    @Override
                    public void onFailure(ParticleCloudException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        refreshButton.performClick();
    }

    public static Intent buildIntent(Context ctx, int value, String deviceid) {
        Intent intent = new Intent(ctx, ValueActivity.class);
        intent.putExtra(ARG_VALUE, value);
        intent.putExtra(ARG_DEVICEID, deviceid);

        return intent;
    }

    public void processValues(int value) {
        DateFormat df = new SimpleDateFormat("h:mm:ss a");
        String timeMsg =  df.format(Calendar.getInstance().getTime());
        tv = (TextView) findViewById(R.id.value);
        alertTitle = (TextView)findViewById(R.id.alertTitle);
        warningImage = (ImageView)findViewById(R.id.warningImage);

        mRelativeLayout = (RelativeLayout)findViewById(R.id.valuelayout);

        String msgToDisplay ="";
        int difference = bridgeLevel - value ;
        if(difference < -5) {
            msgToDisplay = "Water Level Is " + Math.abs(difference) + "  Inches Below Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
            alertTitle.setVisibility(View.GONE);
            warningImage.setVisibility(View.GONE);
        }
        else if(difference>=-5 && difference  <0){
            msgToDisplay = "Water Level Is " + Math.abs(difference) + " Inches Below Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D"));
            alertTitle.setText(R.string.flood_watch_title);
            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);
            warningImage.setVisibility(View.VISIBLE);
        }
        else if(difference  >= 0) {
            msgToDisplay = "Water Level Is " + Math.abs(difference) +  " Inches Above Road Surface  ";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#B71C1C"));
            //alertTitle.setText("Flood Warning");
            alertTitle.setText(R.string.flood_warning_title);
            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);
            warningImage.setVisibility(View.VISIBLE);
        }
        msgToDisplay = msgToDisplay + "\n" +timeMsg;
        tv.setText(msgToDisplay);
        //tv.setTextColor(Color.parseColor("#F44336"));
        tv.setTextColor(Color.WHITE);


    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                    //ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                    int variable;
                    try {
                        variable = mDevice.getIntVariable("in");
                       // if (variable < 100) {
                            // SmsManager smsManager = SmsManager.getDefault();
                            // smsManager.sendTextMessage("5123836659", null, "Flood Alert", null, null);

                       // }
                    } catch (ParticleDevice.VariableDoesNotExistException e) {
                        //Toaster.l(ValueActivity.this, e.getMessage());
                        variable = -1;
                    }
                    return variable;
                }

                @Override
                public void onSuccess(Object i) { // this goes on the main thread
                    int iWaterLevel = ((Integer) i).intValue();
                    processValues(iWaterLevel);
                }

                @Override
                public void onFailure(ParticleCloudException e) {
                    e.printStackTrace();
                }
            });

            ValueActivity.this.mHandler.postDelayed(mRunnable,3000);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Intent upInent = NavUtils.getParentActivityIntent(this);
              //  if(NavUtils.shouldUpRecreateTask(this, upInent)){
                upInent.putParcelableArrayListExtra(DEVICE_LIST, devices);
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upInent).startActivities();
                }
                return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunnable.run();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
    }
}
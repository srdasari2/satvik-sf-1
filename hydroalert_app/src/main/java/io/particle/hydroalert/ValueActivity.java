package io.particle.hydroalert;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

public class ValueActivity extends AppCompatActivity {

    public static final String ARG_VALUE = "ARG_VALUE";
    public static final String ARG_DEVICEID = "ARG_DEVICEID";

    private String email;
    private String password;
    private Button refreshButton;
    private TextView tv;
    int bridgeLevel = 10;
    private TextView alertTitle;
    private ImageView warningImage;
    RelativeLayout mRelativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);
        DateFormat df = new SimpleDateFormat("h:mm:ss a");
        String timeMsg =  df.format(Calendar.getInstance().getTime());
        tv = (TextView) findViewById(R.id.value);
        alertTitle = (TextView)findViewById(R.id.alertTitle);
        warningImage = (ImageView)findViewById(R.id.warningImage);
        int value = getIntent().getIntExtra(ARG_VALUE, 0);
       mRelativeLayout = (RelativeLayout)findViewById(R.id.valuelayout);

        String msgToDisplay ="";
        int difference = bridgeLevel - value ;
        if(difference < -5) {
            msgToDisplay = "Water level is " + Math.abs(difference) + "  inches below Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
            alertTitle.setVisibility(View.GONE);
            warningImage.setVisibility(View.GONE);
        }
        else if(difference>-5 && difference  <0){
            msgToDisplay = "Water level is " + Math.abs(difference) + " inches below or at Road Surface";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D"));
            alertTitle.setText("Flood Watch");
            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);
            warningImage.setVisibility(View.VISIBLE);
        }
        else if(difference  >= 0) {
            msgToDisplay = "Water level is " + Math.abs(difference) +  " inches above Road Surface  ";
            mRelativeLayout.setBackgroundColor(Color.parseColor("#B71C1C"));
            alertTitle.setText("Flood Warning");
            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
            alertTitle.setVisibility(View.VISIBLE);
            warningImage.setVisibility(View.VISIBLE);
        }
        msgToDisplay = msgToDisplay + "\n" +timeMsg;
        tv.setText(msgToDisplay);
        //tv.setTextColor(Color.parseColor("#F44336"));
        tv.setTextColor(Color.WHITE);


        refreshButton = (Button)findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //...
                // Do network work on background thread
                Async.executeAsync(ParticleCloud.get(ValueActivity.this), new Async.ApiWork<ParticleCloud, Object>() {
                    @Override
                    public Object callApi(ParticleCloud ParticleCloud) throws ParticleCloudException, IOException {
                        ParticleDevice device = ParticleCloud.getDevice(getIntent().getStringExtra(ARG_DEVICEID));
                        int variable;
                        try {
                            variable = device.getIntVariable("in");
                            if( variable < 100){
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
                        int  iWaterLevel = ((Integer)i).intValue();
                        int difference = bridgeLevel - iWaterLevel;
                        DateFormat df = new SimpleDateFormat("h:mm:ss a");
                        String timeMsg =  df.format(Calendar.getInstance().getTime());
                        String msgToDisplay = "";
                       // Toast.makeText(ValueActivity.this, "" +difference, Toast.LENGTH_SHORT).show();
                        if(difference < -5) {
                            msgToDisplay = "Water level is " + Math.abs(difference) + "  inches below Road Surface";
                            mRelativeLayout.setBackgroundColor(Color.parseColor("#4CAF50"));
                            alertTitle.setVisibility(View.GONE);
                            warningImage.setVisibility(View.GONE);
                        }
                        else if(difference>-5 && difference  <0){
                            msgToDisplay = "Water level is " + Math.abs(difference) + " inches below or at Road Surface";
                            mRelativeLayout.setBackgroundColor(Color.parseColor("#FBC02D"));
                            alertTitle.setText("Flood Watch");
                            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
                            alertTitle.setVisibility(View.VISIBLE);
                            warningImage.setVisibility(View.VISIBLE);
                        }
                        else if(difference  >= 0) {
                            msgToDisplay = "Water level is " + Math.abs(difference) +  " inches above Road Surface  ";
                            mRelativeLayout.setBackgroundColor(Color.parseColor("#B71C1C"));
                            alertTitle.setText("Flood Warning");
                            warningImage.setImageResource(R.drawable.ic_warning_black_48dp);
                            alertTitle.setVisibility(View.VISIBLE);
                            warningImage.setVisibility(View.VISIBLE);
                        }
                        msgToDisplay = msgToDisplay + "\n" +timeMsg;
                        tv.setText(msgToDisplay);
                        //tv.setTextColor(Color.parseColor("#F44336"));
                        tv.setTextColor(Color.WHITE);
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

    public void processValues(int waterLevel)
    {

    }


}

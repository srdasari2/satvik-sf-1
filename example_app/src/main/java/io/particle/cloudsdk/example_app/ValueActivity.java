package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class ValueActivity extends AppCompatActivity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";

    private String email;
    private String password;
    private Button refreshButton;
    private TextView tv;
    int bridgeLevel = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value);
        tv = (TextView) findViewById(R.id.value);
        int value = getIntent().getIntExtra(ARG_VALUE, 0);
        String msgToDisplay ="";
        int difference = bridgeLevel - value ;
        if(difference < 0){
            msgToDisplay = "Water level is " + Math.abs(difference) + "  inches below Bridge/Road Surface";
        }
        else if(difference  > 0){
            msgToDisplay = " Water level is " + Math.abs(difference) + "inches   Above Bridge/Road Surface";
        }
        else if(difference == 0) {
            msgToDisplay = "Water is at Bridge/Road Surface  ";
        }
        tv.setText(String.valueOf(getIntent().getIntExtra(ARG_VALUE, 0)));

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
                            Toaster.l(ValueActivity.this, e.getMessage());
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
                        Toast.makeText(ValueActivity.this, "" +difference, Toast.LENGTH_SHORT).show();
                        if(difference < 0){
                            msgToDisplay = "Water level is " + Math.abs(difference) + "  inches below Bridge/Road Surface  ";
                        }
                        else if(difference  > 0){
                            msgToDisplay = " Water level is " + Math.abs(difference) + "inches   Above Bridge/Road Surface   ";
                        }
                        else if(difference == 0) {
                            msgToDisplay = "Water is at Bridge/Road Surface    ";
                        }
                        msgToDisplay = msgToDisplay + timeMsg;
                        tv.setText(msgToDisplay);
                        tv.setTextColor(Color.parseColor("#F44336"));
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


}

package io.particle.hydroalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

import static io.particle.hydroalert.ValueActivity.ARG_DEVICEID;
import static io.particle.hydroalert.ValueActivity.ARG_VALUE;

public class ListActivity extends AppCompatActivity {
    public static final String DEVICE_LIST = "DEVICE_LIST";
    private ArrayList<ParticleDevice> deviceList= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ListView listView = (ListView)findViewById(R.id.devicelist);
        deviceList = getIntent().getParcelableArrayListExtra(DEVICE_LIST);
        String[]  deviceNames = extractDeviceNames(deviceList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, deviceNames);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Async.executeAsync(ParticleCloud.get(ListActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

                    private ParticleDevice mDevice;
                    int distance;

                    @Override
                    public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {

                        mDevice = deviceList.get(position); //Get the device handle using deviceId

                        try {
                            distance = mDevice.getIntVariable("in");  //Read the distance from Cloud
                            Log.d("Distance", "IN: " + distance);
                        } catch (ParticleDevice.VariableDoesNotExistException e) {
                            Log.e("Error", "Variable does not exist");
                        }

                        return -1;

                    }

                    @Override
                    public void onSuccess(Object value) {
                        Intent intent = new Intent(ListActivity.this, ValueActivity.class);
                        intent.putExtra(ARG_VALUE, distance);
                         intent.putExtra(ARG_DEVICEID, mDevice.getID());
                        intent.putExtra("device", mDevice);
                        intent.putParcelableArrayListExtra(DEVICE_LIST, deviceList);
                       startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(ParticleCloudException e) {
                        Log.d("Login failed", e.getBestMessage());
                        e.printStackTrace();
                        Log.d("info", e.getBestMessage());
                    }
                });

         }
        });

    }

    private String[] extractDeviceNames(ArrayList<ParticleDevice> cloudDevices){
       String[] devices = new String[cloudDevices.size()];
        for(int i=0;i<cloudDevices.size()-1;i++){
            devices[i] = cloudDevices.get(i).getName();
        }
        return devices;
    }
}

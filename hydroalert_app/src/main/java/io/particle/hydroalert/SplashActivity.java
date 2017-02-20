package io.particle.hydroalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.cimosys.basic.encryption.util.CipherHelper;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

import static io.particle.hydroalert.ValueActivity.ARG_DEVICEID;
import static io.particle.hydroalert.ValueActivity.ARG_VALUE;


public class SplashActivity extends AppCompatActivity {

    private String email;
    private String password;
    private ProgressBar progressBar;
    SharedPreferences SP;
    EncryptionSetupReources encryptionSetupReources;
    CipherHelper cipherHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SP = getApplication().getSharedPreferences("encryption", Context.MODE_PRIVATE);
        SP.edit().putString("CipherPwd", getString(R.string.cipher_password)).commit();
        cipherHelper = new CipherHelper(SP.getString("CipherPwd", null));

        SP.edit().putString("username",  "j4tKVc6tfPL6xbfbRVC7Jq+XfUpkMy74").commit();
        SP.edit().putString("password", "kV5cnTROajGXNXQKv1a/Qg==").commit();

        encryptionSetupReources = EncryptionSetupReources.getInstance(getApplicationContext());



        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        login();

    }


    private void login() {
        email = cipherHelper.decrypt(SP.getString("username", null));
        password = cipherHelper.decrypt(SP.getString("password", null));

        Async.executeAsync(ParticleCloud.get(SplashActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

            private ParticleDevice mDevice;
            int distance;

            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                sparkCloud.logIn(email, password); //Login to the IoT cloud
                sparkCloud.getDevices();
                mDevice = sparkCloud.getDevice(getString(R.string.deviceid)); //Get the device handle using deviceId

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
                Log.d("Login Successful", "Logged in");
                Intent intent = new Intent(SplashActivity.this, ValueActivity.class);
                intent.putExtra(ARG_VALUE, distance);
                intent.putExtra(ARG_DEVICEID, mDevice.getID());
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


}

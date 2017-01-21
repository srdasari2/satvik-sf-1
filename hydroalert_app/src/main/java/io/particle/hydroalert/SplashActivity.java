package io.particle.hydroalert;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        login();

    }


    private void login() {
        // try {

        //Log.d("Encrypted username : ", EncryptionSetupReources.getInstance(getApplicationContext()).aesCiph.encrypt("srdasari1@gmail.com"));
        //Log.d("Encrypted username : ", EncryptionSetupReources.getInstance(getApplicationContext()).aesCiph.encrypt("bluealerttx@gmail.com"));
        //email = EncryptionSetupReources.getInstance(getApplicationContext()).aesCiph.decrypt(getString(R.string.userName));
        //password = EncryptionSetupReources.getInstance(getApplicationContext()).aesCiph.decrypt(getString(R.string.password));

        email = "bluealerttx@gmail.com";
        password = "Satviklaya";
//
//        } catch (SimpleCipher.EncryptionException e) {
//            e.printStackTrace();
//        }


        Async.executeAsync(ParticleCloud.get(SplashActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

            private ParticleDevice mDevice;
            int distance;

            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                sparkCloud.logIn(email, password);
                sparkCloud.getDevices();
                mDevice = sparkCloud.getDevice(getString(R.string.deviceid));

                try {
                    distance = mDevice.getIntVariable("in");
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

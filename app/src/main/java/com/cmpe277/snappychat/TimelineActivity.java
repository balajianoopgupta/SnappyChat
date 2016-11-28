package com.cmpe277.snappychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class TimelineActivity extends AppCompatActivity {
    private static final String TAG = "Timeline Acitivity";
    Button logOut;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        logOut = (Button) findViewById(R.id.logOutBtn);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginActivity.googleSignOut();
                startActivity(new Intent(TimelineActivity.this,LoginActivity.class));

            }
        });
    }

}

package com.example.snehamunden.bikenroll;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Button openMaps;
    private Button openNews;
    private Button openFit;
    private Button openHist;
    private String useremail;
//    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
         useremail = extras.getString("useremail");
        openMaps = (Button)findViewById(R.id.button);
        openNews = (Button)findViewById(R.id.newsBtn);
        openFit = (Button)findViewById(R.id.fit);
        openHist = (Button)findViewById(R.id.history);

        openMaps.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.button) {

            try{
                Log.d(TAG, "useremail 1313: "+useremail);
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                i.putExtra("useremail",useremail);
                startActivity(i);
            }catch (Exception e){
                Log.d(TAG, "ellol: "+e);
            }

                }
            }
        });

        openNews.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.newsBtn) {

                    try{
                        Intent i = new Intent(MainActivity.this, NewsActivity.class);
                        i.putExtra("useremail",useremail);
                        startActivity(i);
                    }catch (Exception e){
                        Log.d(TAG, "ellol: "+e);
                    }

                }
            }
        });


        openHist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.history) {

                    try{
                        Log.d(TAG, "useremail 1313: "+useremail);
                        Intent i = new Intent(MainActivity.this, HistoryActivity.class);
                        i.putExtra("useremail",useremail);
                        startActivity(i);
                    }catch (Exception e){
                        Log.d(TAG, "ellol: "+e);
                    }

                }
            }
        });

//        openFit.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (v.getId() == R.id.fit) {
//
//                    try{
//                        Intent i = new Intent(MainActivity.this, FitActivity.class);
//                        i.putExtra("useremail",useremail);
//                        startActivity(i);
//                    }catch (Exception e){
//                        Log.d(TAG, "ellol: "+e);
//                    }
//
//                }
//            }
//        });

        Button signOutButton = (Button) findViewById(R.id.button2);

        findViewById(R.id.button2).setOnClickListener(this);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button2:
                signOut();
                break;

        }
    }

    private void signOut() {

        AuthenticatorActivity.mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent i = new Intent(MainActivity.this, AuthenticatorActivity.class);
//                    i.putExtra("scansno",scanContent);
                        startActivity(i);
                    }
                });
    }

}

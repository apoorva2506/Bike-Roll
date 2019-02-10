package com.example.snehamunden.bikenroll;

import android.arch.core.internal.FastSafeIterableMap;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;
import android.widget.Button;

import static android.support.constraint.Constraints.TAG;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class FitActivity extends AppCompatActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1;
    private Button openFit;
    private String useremail = SaveSharedPreference.getUserName(FitActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fit);
        openFit = (Button)findViewById(R.id.getFitness);


        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .build();

        Log.d(TAG, "GoogleSignIn "+GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this)));
        Log.d(TAG, "fitnessOptions "+fitnessOptions);
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            Log.d(TAG, "before req permissionns: ");

            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
            Log.d(TAG, "after req permissionns: ");

        } else {
            Log.d(TAG, "inelse: ");
            subscribe();

        }


        openFit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.getFitness) {

                    try{
                        accessGoogleFit();
                        Log.d(TAG, "resp: ");

                    }catch (Exception e){
                        Log.d(TAG, "ellol in access: "+e);
                    }

                }
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == FitActivity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                subscribe();
            }
        }
    }

    private void subscribe() {

        Log.d(TAG, "google wala client: "+GoogleSignIn.getLastSignedInAccount(this));
        Task<Void> response = Fitness.getRecordingClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Successfully subscribed!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "There was a problem subscribing.");
                    }
                });

    }

    private void accessGoogleFit() {
        Log.d(TAG, "anday ya nahi????????: ");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        DataReadRequest readRequest;
        Log.d(TAG, "anday ya nahi1????????: ");

        try {
            readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .bucketByTime(1, TimeUnit.MILLISECONDS)
                    .build();
            Log.d(TAG, "anday ya nahi2????????: ");



            Task<DataReadResponse> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .readData(readRequest)
                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                        @Override
                        public void onSuccess(DataReadResponse dataReadResponse) {
                            Log.d(TAG, "wowwww ");
                            Log.d(TAG, "onSuccess()");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure()", e);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<DataReadResponse> task) {
                            Log.d(TAG, "onComplete()");
                        }
                    });


//             readRequest = new DataReadRequest.Builder()
//                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                     .bucketByTime(1, TimeUnit.MILLISECONDS)
//                    .build();
//            Log.d(TAG, "readRequest: "+readRequest);
//
//            Task<DataReadResponse> response = Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).readData(readRequest)
//                    .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                        @Override
//                        public void onSuccess(DataReadResponse dataReadResponse) {
//                            Log.d(TAG, "onSuccess()");
//                            Log.d(TAG, "dresp : " + dataReadResponse.getBuckets());
//                            Log.d(TAG, "dresp : " + dataReadResponse.getDataSets());
//                            Log.d(TAG, "dresp : " + dataReadResponse.getStatus());
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e(TAG, "onFailure()", e);
//                        }
//                    })
//                    .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataReadResponse> task) {
//                            List<DataSet> dataSets = response.getResult().getDataSets();
//
//                            Log.d(TAG, "accessGoogleFit: "+response);
//                            Log.d(TAG, "dresp : " + response.getResult().getDataSets());
//                            Log.d(TAG, "onComplete()");
//                        }
//                    });;
//

//        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                .readData(readRequest)
//                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
//                    @Override
//                    public void onSuccess(DataReadResponse dataReadResponse) {
//                        Log.d(TAG, "onSuccess()");
//                            Log.d(TAG, "dresp : " + dataReadResponse.getBuckets());
//                            Log.d(TAG, "dresp : " + dataReadResponse.getDataSets());
//                            Log.d(TAG, "dresp : " + dataReadResponse.getStatus());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "onFailure()", e);
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DataReadResponse> task) {
//                        Log.d(TAG, "onComplete()");
//                    }
//                });


        }
        catch (Exception e) {
            Log.d(TAG, "excp: "+e);
        }
    }


}

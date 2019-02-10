package com.example.snehamunden.bikenroll;

import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
//import com.google.android.gms.auth.api.signin;

import static android.support.constraint.Constraints.TAG;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;

public class FetchSteps extends AsyncTask<Void, Void, Integer>  {

    private GoogleSignInAccount context;
    private int totalSteps;


    @Override
    protected Integer doInBackground(Void... voids) {
        return getStepsCountFromFIT();
    }

    protected void onPostExecute(Integer steps) {
        super.onPostExecute(steps);
        // Store #endTime in shared preferene for next time use.
//        stepUpdateListener.onStepUpdate(steps, startTime, endTime);

    }
    public FetchSteps(GoogleSignInAccount context){
        this.context=context;
    }

    private int getStepsCountFromFIT() {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();

//        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//                .bucketByTime(1, TimeUnit.MILLISECONDS)
//                .build();
//            Log.d(TAG, "readRequest: "+readRequest);
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

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

//        DataReadResponse dataReadResult = Fitness.getHistoryClient(context, readRequest)
//                .await(1, TimeUnit.MINUTES);


        //Used for aggregated data
//        if (dataReadResult.getBuckets().size() > 0) {
//            Log.i(TAG, "Get Buckets");
//            for (Bucket bucket : dataReadResult.getBuckets()) {
//                List<DataSet> dataSets = bucket.getDataSets();
//                for (DataSet dataSet : dataSets) {
//                    showDataSet(dataSet);
//                }
//            }
//        }
//        //Used for non-aggregated data
//        else if (dataReadResult.getDataSets().size() > 0) {
//            Log.i(TAG, "Get DataSet");
//            for (DataSet dataSet : dataReadResult.getDataSets()) {
//                showDataSet(dataSet);
//            }
//        } else {
//            Log.i(TAG, "No history found for this user");
//        }
        return totalSteps;
    }

    private void addSteps(int step) {
        Log.e(TAG, step + "");
        totalSteps += step;
    }

    /**
     * Get Field from DataPoint and DataPoint from DataSource
     * @param dataSet
     */
    private void showDataSet(DataSet dataSet) {
        int steps = 0;
        for (DataPoint dp : dataSet.getDataPoints()) {
            for (Field field : dp.getDataType().getFields()) {
                steps = dp.getValue(field).asInt();
                addSteps(steps);
            }
        }
    }


}

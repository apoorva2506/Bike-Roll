package com.example.snehamunden.bikenroll;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;


import java.io.File;
import java.util.concurrent.ScheduledExecutorService;

import location.BikenrolleeClient;

public class NewsActivity extends AppCompatActivity {

    private BikenrolleeClient apiClient;

    private static final String TAG = NewsActivity.class.getSimpleName();
    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;
    private String useremail;

    private ImageButton playButton;
    private ImageButton stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        useremail = extras.getString("useremail");
        setContentView(R.layout.activity_news);

        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        apiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance())
                .build(BikenrolleeClient.class);
        Log.d(TAG, "apiclient: yeh hai apiclients");

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d(TAG, "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        playButton = (ImageButton)findViewById(R.id.play);
        stopButton = (ImageButton)findViewById(R.id.stop);
        final MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            Uri myUri = Uri.fromFile(new File("/storage/emulated/0/Bluetooth/newshere.mp3")); // initialize Uri here
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
        }
        catch (Exception e) {
            Log.d(TAG, "ellol in mp: "+e);

        }

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.play) {

                    try{
                        Log.d(TAG, "playing news: ");
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Log.d(TAG, "it started: ");

                    }catch (Exception e){
                        Log.d(TAG, "ellol1: "+e);
                    }

                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.stop) {

                    try{
                        Log.d(TAG, "stop news: ");
//                        mediaPlayer.prepare();
                        mediaPlayer.stop();
                        Log.d(TAG, "it started: ");

                    }catch (Exception e){
                        Log.d(TAG, "ellol2: "+e);
                    }

                }
            }
        });

        downloadWithTransferUtility();

    }

    private void downloadWithTransferUtility() {
        Log.d(TAG, "andar: ");

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance())).build();
        Log.d(TAG, "cp1: ");

        TransferObserver downloadObserver =
                transferUtility.download(
                        "soundfile.mp3",
                        new File("/storage/emulated/0/Bluetooth/newshere.mp3"));
        Log.d(TAG, "cp2: ");

        // Attach a listener to the observer to get state update and progress notifications
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "listen bro: ");
                if (TransferState.COMPLETED == state) {
                    Log.d(TAG, "what if: ");
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, "onProgressChanged: ");
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.d("Your Activity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.d(TAG, "this is error: "+ex);

                // Handle errors
            }

        });

        // If you prefer to poll for the data, instead of attaching a
        // listener, check for the state and progress in the observer.
        if (TransferState.COMPLETED == downloadObserver.getState()) {
            // Handle a completed upload.
        }

        Log.d("Your Activity", "Bytes Transferred: " + downloadObserver.getBytesTransferred());
        Log.d("Your Activity", "Bytes Total: " + downloadObserver.getBytesTotal());
    }
}

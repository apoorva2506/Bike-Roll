package com.example.snehamunden.bikenroll;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import location.BikenrolleeClient;



public class RidinActivity extends AppCompatActivity  {

    private Button endRide;
    private String useremail;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private static final String TAG = RidinActivity.class.getSimpleName();
    private LocationManager locationManager;
    private android.location.LocationListener myLocationListener;
    private double latitude,longitude;
    private BikenrolleeClient apiClient;
    private Button openNews;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridin);

        Bundle extras = getIntent().getExtras();
         useremail = extras.getString("useremail");
        Log.d(TAG, "useremailsaf: "+useremail);
        openNews = (Button)findViewById(R.id.news);

        endRide = (Button) findViewById(R.id.endRide);

        // Create the client
        apiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance())
                .build(BikenrolleeClient.class);
        Log.d(TAG, "apiclient: yeh hai apiclients");


        // Initialize the AWS Mobile Client
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d(TAG, "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        endRide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.endRide) {

                    try {
                        Log.d(TAG, "check start: ");
                        checkLocation();
                        Log.d(TAG, "check done: ");

                        Bundle extras = getIntent().getExtras();
                        String bikeid = extras.getString("bikeid");
                        useremail = extras.getString("useremail");
                        Log.d(TAG, "useremail 534: " + useremail);
                        Log.d(TAG, "bikeid 535: " + bikeid);
                        Log.d(TAG, "lat: " + latitude);
                        Log.d(TAG, "long: " + longitude);
                        doInvokeAPI(bikeid,useremail,latitude,longitude,"endride");
                        Intent i = new Intent(RidinActivity.this, MainActivity.class);
                        i.putExtra("useremail",useremail);
                        startActivity(i);

                    } catch (Exception e) {
                        Log.d(TAG, "ellol1: " + e);
                    }

                }
            }
        });

        openNews.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.news) {

                    try{
                        Intent i = new Intent(RidinActivity.this, NewsActivity.class);
                        i.putExtra("useremail",useremail);
                        startActivity(i);
                    }catch (Exception e){
                        Log.d(TAG, "ellol: "+e);
                    }

                }
            }
        });
    }


    public void doInvokeAPI(String bikeId, String username, double lat, double lon, String status) {
        // Create components of api request
        final String method = "POST";
        final String path = "/location";

        final String body = "{\n" +
                "    \"useremail\" : \""+username+"\",\n" +
                "    \"bikeid\" : \""+bikeId+"\",\n" +
                "    \"latitude\" : \""+lat+"\",\n" +
                "    \"longitude\" : \""+lon+"\",\n" +
                "    \"ridestatus\" : \"endride\"\n" +
                "}";
        Log.d(TAG, "doInvokeAPI end : "+body);
        final byte[] content = body.getBytes(StringUtils.UTF8);

        final Map parameters = new HashMap<>();
        parameters.put("lang", "en_US");

        final Map headers = new HashMap<>();

        // Use components to create the api request
        ApiRequest localRequest =
                new ApiRequest(apiClient.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json");
//                        .withParameters(parameters);

        // Only set body if it has content.
        if (body.length() > 0) {
            localRequest = localRequest
                    .addHeader("Content-Length", String.valueOf(content.length))
                    .withBody(content);
        }

        final ApiRequest request = localRequest;

        // Make network call on background thread

        ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
        taskExecutor.execute(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG,
                            "Invoking API w/ Request endride: " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    Log.d(TAG, "ye hai we bada endride resp : " + response);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        Log.d(TAG, "we in endride Response : " + responseData);
                    }

                    Log.d(TAG, response.getStatusCode() + " " + response.getStatusText());


                } catch (final Exception exception) {
                    Log.d(TAG, "we in error endride brooo : " + exception);

                    Log.e(TAG, exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }));
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "we be in endride catch : " );

        }
//        Log.d(TAG, "printing json after thread body: " + json);
    }
    public void checkLocation() {

        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);


        if (ActivityCompat.checkSelfPermission(RidinActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RidinActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "hi: ");
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d(TAG, "latti3: " + latitude);
                Log.d(TAG, "longi3: " + longitude);
            }
        }
        Log.d(TAG, "beforerdf: ");
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, myLocationListener);
//
//        Log.d(TAG, "latti1: " + latitude);
//        Log.d(TAG, "longi1: " + longitude);
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}

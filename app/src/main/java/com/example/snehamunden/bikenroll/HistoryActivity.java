package com.example.snehamunden.bikenroll;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
// TODO Replace this with your api friendly name and client class name
import location.BikenrolleeClient;
import com.amazonaws.mobileconnectors.apigateway.ApiClientFactory;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import location.BikenrolleeClient;

import static com.amazonaws.services.cognitoidentityprovider.model.AttributeDataType.DateTime;

public class HistoryActivity extends AppCompatActivity implements MyAdapter.ItemClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private BikenrolleeClient apiClient;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String useremail;
    MyAdapter adapter;

//    private JSONArray json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
         useremail = extras.getString("useremail");

        // Create the client
        apiClient = new ApiClientFactory()
                .credentialsProvider(AWSMobileClient.getInstance())
                .build(BikenrolleeClient.class);
        Log.d(TAG, "apiclient: yeh hai apiclients");

        Log.d(TAG, "useremail 23232: "+useremail);

        doInvokeAPI(useremail);
        // Initialize the AWS Mobile Client
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d(TAG, "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

    }

    public void doInvokeAPI(String username) {
        // Create components of api request
        final String method = "POST";
        final String path = "/location";

        final String body = "{\"useremail\":\""+username+"\"}";
        Log.d(TAG, "doInvokeAPI: "+body);
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
                            "Invoking API w/ Request body: " +
                                    request.getHttpMethod() + ":" +
                                    request.getPath());

                    final ApiResponse response = apiClient.execute(request);

                    Log.d(TAG, "ye hai we bada hist resp : " + response);

                    final InputStream responseContentStream = response.getContent();

                    if (responseContentStream != null) {
                        final String responseData = IOUtils.toString(responseContentStream);
                        Log.d(TAG, "we in hist Response : " + responseData);
                        JSONArray json = new JSONArray(responseData);
                        Log.d(TAG, "printing hist json : " + json);
                        displayRides(json);
                        Log.d(TAG, "ye raha updateLocationUI k baad wala hist json: " + json);
                    }

                    Log.d(TAG, response.getStatusCode() + " " + response.getStatusText());


                } catch (final Exception exception) {
                    Log.d(TAG, "we in error hist brooo : " + exception);

                    Log.e(TAG, exception.getMessage(), exception);
                    exception.printStackTrace();
                }
            }
        }));
        taskExecutor.shutdown();
        try {
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Log.d(TAG, "we be in hist catch : " );

        }
    }

    private void displayRides(JSONArray json) {
        Log.d(TAG, "we in here: " + json);
        ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

        try {
            Log.d(TAG, "we be in displayRides: " + json.length());
            Log.d(TAG, "Json Array is: " + json);

            String start = "", bikeid = "", end = "", dist="",startLoc="";

            for (int i = 0; i < json.length(); i++) {
                Log.d(TAG, "we be in setMarkers:[ " + i);
                JSONObject jsonObject1 = json.getJSONObject(i);
                Log.d(TAG, "yeh hai jsponobject:[ " + jsonObject1);
                bikeid = jsonObject1.getString("Bikeid");
                start = jsonObject1.getString("Starttime");
                double startLoclat = Double.parseDouble(jsonObject1.getString("Startlat"));
                double startLoclng = Double.parseDouble(jsonObject1.getString("Startlng"));
                double endlat = Double.parseDouble(jsonObject1.getString("Endlat"));
                double endlng = Double.parseDouble(jsonObject1.getString("Endlng"));

                Geocoder geocoder;
                List<Address> strtadd, endadd;
                geocoder = new Geocoder(this, Locale.getDefault());

                strtadd = geocoder.getFromLocation(startLoclat, startLoclng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                endadd = geocoder.getFromLocation(endlat, endlng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String startaddress = strtadd.get(0).getAddressLine(0);
                String endaddress = endadd.get(0).getAddressLine(0);

                end = jsonObject1.getString("Endtime");
                dist = jsonObject1.getString("Distance");
                Log.d(TAG, "printing json bikeid1: " + bikeid);

                ArrayList<String> details = new ArrayList<String>();
                details.add(startaddress);
                details.add(endaddress);
                details.add(dist);
                details.add(start);
                details.add(end);

                details.add(dist);

                list.add(details);


            }
        } catch (Exception e) {
            Log.d(TAG, "ye raha error: " + e);
        }
        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked on row number " + position, Toast.LENGTH_SHORT).show();
    }

}

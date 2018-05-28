package com.github.ros_java.test_android.sensor_serial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.anastr.speedviewlib.SpeedView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ros.android.AppCompatRosActivity;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.BitmapFromImage;
import org.ros.android.view.RosImageView;
import org.ros.message.MessageListener;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import in.unicodelabs.kdgaugeview.KdGaugeView;
import sensor_msgs.CompressedImage;
import sensor_msgs.Image;

import static com.github.ros_java.test_android.sensor_serial.Listener.imageSub;

public class VisualizationActivity extends AppCompatRosActivity implements OnMapReadyCallback ,DirectionCallback {
    private static final LatLngBounds GUC_BOUNDS = new LatLngBounds(new LatLng(29.9842014, 31.4387794),
            new LatLng(29.9899635, 31.4445531));
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates;
    LocationRequest mLocationRequest;
    LatLng mLastLocation;
    private ArrayList<Polyline> polylines;
    private LatLng loc;
    KdGaugeView speedoMeterView;
    private LocalBroadcastManager broadcaster;
    Runnable updater;
    static RosImageView<CompressedImage> videoStreamView;
//    RosImageView<Image> imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization);
        getSupportActionBar().hide();
        broadcaster = LocalBroadcastManager.getInstance(this);

        //MapsFragment MapsFragment1 = new MapsFragment();
        //Fragment MapsFragment1 = getFragmentManager().findFragmentById(R.id.mapFragment);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, MapsFragment, "").commit();
       //View Map= findViewById(R.id.mapFragment);
        //Fragment fragment = getFragmentManager().findFragmentById(R.id.mapFragment);
//        GoogleMap map = ((SupportMapFragment) getFragmentManager()
//                .findFragmentById(R.id.map)).getMap();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v("osama", "I reached callback");
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mLastLocation));
                    Log.v("osama", mLastLocation.toString());
                }
            }

        };
        //SupportMapFragment mGoogleMap = ((SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map1));
        MapFragment mGoogleMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.map1));

        //MapFragment mGoogleMap = (MapFragment) getFragmentManager() .findFragmentById(R.id.map);
        mGoogleMap.getMapAsync(this);

        Button endButton = findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEndButton();
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("FcmData")
        );
//        SpeedView speedometer = findViewById(R.id.speedView);
//        View bottomSheet=findViewById(R.id.BottomSheet_layout);
//        bottomSheet.setVisibility(View.GONE);

//        View bottomSheet2=findViewById(R.id.BottomSheet_layout2);
//        bottomSheet2.setVisibility(View.GONE);
        //MapsFragment.mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //ImageView visualizationImage=(ImageView) findViewById(R.id.visualizationImage);
        videoStreamView = (RosImageView<CompressedImage>) findViewById(R.id.visualizationImage);
        videoStreamView.setTopicName("camright/raw/compressed");
        videoStreamView.setMessageType(CompressedImage._TYPE);
        videoStreamView.setMessageToBitmapCallable(new BitmapFromCompressedImage());
        imageSub.addMessageListener(new MessageListener<Image>() {
            @Override
            public void onNewMessage(Image image) {
                Log.d("dodo", "I came here");
            }
        });






    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap = googleMap;
        mMap.setMinZoomPreference(16);
        mMap.setLatLngBoundsForCameraTarget(GUC_BOUNDS);
        LatLng latLng = new LatLng(29.9867788, 31.441697);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        loc = bundle.getParcelable("destination");
        createLocationRequest();
        startLocationUpdates();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GetRoutToMarker(loc);
            }
        }, 1000);
        speedoMeterView = (KdGaugeView)findViewById(R.id.speedMeter);


        final Handler timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                speedoMeterView.setSpeed(Integer.valueOf(MapsFragment.speedValue));
                timerHandler.postDelayed(updater,1000);
            }
        };
        timerHandler.post(updater);
//        while (true) {
//            speedoMeterView.setSpeed(Integer.valueOf(MapsFragment.speedValue));
//        }

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                speedoMeterView.setSpeed(Integer.valueOf(MapsFragment.speedValue));
//            }
//        }, 0, 1000);//put here time 1000 milliseconds=1 second
//



    }
    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mLastLocation!=null) {
                    GetRoutToMarker(loc);
                }
            }
        }, 2000);
    }

    private void GetRoutToMarker(LatLng clickedMarker) {
        //ClickM = clickedMarker;
        GoogleDirection.withServerKey("AIzaSyCZ5TH2mfl26LqDq6kkVbo85gLPZ9fmaik")
                .from(mLastLocation)
                .to(clickedMarker)
                .transportMode(TransportMode.DRIVING)
                .execute(this);

    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        Log.d("Polylines", direction.getStatus());

        if (direction.isOK()) {
            Log.d("Polylines", "eh b2aa");
            Log.d("Polylines", String.valueOf(direction.getRouteList().get(0)));
            Route route = direction.getRouteList().get(0);
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            //mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 4, Color.BLACK));
            polylines = new ArrayList<>();
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLACK);
            polyOptions.width(6);
            polyOptions.addAll(directionPositionList);
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
           // setCameraWithCoordinationBounds(route);
        }
        
    }

    @Override
    public void onDirectionFailure(Throwable t) {


    }


    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //checkLocationPermission();
            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String status = bundle.getString("EVENT");
            String carID = bundle.getString("CAR_ID");
            String tripID=bundle.getString("TRIP_ID");
            //Log.d("eh ya status da?", status);
            switch (status) {

                case "NEW":
                   // getTripFromServer();
                    break;
                case "CANCEL":
                    //onCancel();
                    break;
                case "END":
                    break;
                case "START":
                    //startTripFromServer();
                    break;
                case "MODIFY":
                    break;

            }
        }

    };
    public void onEndButton(){
        MapsFragment.confirmRide.setClickable(true);
        Intent intent = new Intent("onEnd");
        intent.putExtra("EVENT", "ResetAll");
        broadcaster.sendBroadcast(intent);
        this.finish();
        String cancelUrl = "https://sdc-trip-car-management.herokuapp.com/car/trip/end/tablet/hadwa";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, cancelUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject responseObject = response.getJSONObject(i);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

         //Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MapsFragment.confirmRide.setClickable(true);
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());

        nodeConfiguration.setNodeName("camright/raw");
        nodeMainExecutor.execute(videoStreamView, nodeConfiguration);
    }
}

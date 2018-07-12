package com.github.ros_java.test_android.sensor_serial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnShowAlertListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ros.android.view.visualization.VisualizationView;
import org.ros.message.MessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import std_msgs.String;

import static com.github.ros_java.test_android.sensor_serial.Listener.subscriber;


//import static com.example.hadwa.myapplication.R.drawable.ic_marker_black;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener,DirectionCallback, GoogleMap.OnMarkerClickListener, ListListener {
    static List<TripDestination> modifyDestinations;
    static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates;
    static LatLng mLastLocation;
    private LatLng Dest1 = new LatLng(29.988428, 31.4389311);
    static HashMap<java.lang.String, LatLng> pinLocations;
    static MyList chosenMarkerArrayList;
    static ArrayList<Marker> AllMarkersArrayList;
    static boolean localTrip = true;
    private ArrayList<java.lang.String> zeftMarkers;

    static TextView markerText;
    static View markerIcon;
    static java.lang.String appState;
    RequestTrip requestTrip;
    private final static java.lang.String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    public static final int REQUEST_CHECK_SETTINGS = 10;
    private static final LatLngBounds GUC_BOUNDS = new LatLngBounds(new LatLng(29.9842014, 31.4387794),
            new LatLng(29.9899635, 31.4445531));

    static Trip createdTrip;
    static Trip tripToBe;
    static ImageView markerView;
    static TextView BottomSheetText;
    private View view;
    static List<java.lang.String> Markers;
    static RecyclerView recyclerView;
    public static int DestinationCount = 0;
    View cardLayout;
    private int backpress;
    static LinearLayout bottomSheet;
    static LinearLayout bottomSheet2;
    private static ArrayList<Polyline> polylines;
    private static Gson gson;
    boolean markersRequested = false;
    static java.lang.String tripState;
    static Button confirmRide;
    static java.lang.String speedValue;
     static Button edit;
    static Button Canceltrip;
    private java.lang.String tripUrl;
    CardView  destCard;
    TextView destCardText ;
    Button destCradBtn;
    static java.lang.String modifyState;
    private boolean tmam;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.maps_fragment, container, false);
        markerIcon = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
//         markerIcon = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        appState = "initialState";
        CheckInternet();
//        getTripFromServer();
        AllMarkersArrayList = new ArrayList<Marker>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //NewText = (TextView)findViewById(R.id.WhichStop);
        zeftMarkers = new ArrayList<java.lang.String>();
        tripState = "localTrip";
        requestTrip = new RequestTrip();
        createdTrip = new Trip();
        tripToBe = new Trip();
        markerView = (ImageView) markerIcon.findViewById(R.id.icon1);
        //TODO view wala eh
        destCard = (CardView) markerIcon.findViewById(R.id.destCard);
         destCardText =(TextView) view.findViewById(R.id.destCardText);
         destCradBtn =(Button)view.findViewById(R.id.Continue);


        polylines = new ArrayList<>();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v("osama", "I reached callback");
                for (Location location : locationResult.getLocations()) {
                    mLastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    Log.v("osama", mLastLocation.toString() + "");
                }
            }

        };



        //BottomSheetText.setText("Pick a drop-off location");
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        bottomSheet = (LinearLayout) view.findViewById(R.id.BottomSheet_layout);
        bottomSheet2 = (LinearLayout) view.findViewById(R.id.BottomSheet_layout2);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Markers = new ArrayList<>();
        chosenMarkerArrayList = new MyList(this);
        pinLocations = new HashMap<java.lang.String, LatLng>();

        RecyclerListAdapter adapter = new RecyclerListAdapter(getActivity(), Markers);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callBack = new ItemDragHelper(adapter);
        ItemTouchHelper dragHelper = new ItemTouchHelper(callBack);
        dragHelper.attachToRecyclerView(recyclerView);

        //IntentFilter filter = new IntentFilter();
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver((mMessageReceiver),
                new IntentFilter("FcmData")
        );
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver((mMessageReceiver2),
                new IntentFilter("onEnd")
        );


        updateFCMServer();

//        subscriber.addMessageListener(new MessageListener<String>() {
//            @Override
//            public void onNewMessage(std_msgs.String s) {
//                speedValue = s.getData();
//               // Log.d("listener-log","I heard: \"" + s.getData() + "\"");
//            }
//        });


        return view;

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
            java.lang.String status = bundle.getString("EVENT");
            java.lang.String carID = bundle.getString("CAR_ID");
            java.lang.String tripID=bundle.getString("TRIP_ID");
           // Log.d("Trip-Status-FCM", status);
            switch (status) {

                case "CAR_ON_WAY":
                    getTripFromServer();
                    break;
                case "CANCEL":
                    Log.d("appState", appState);
                    if(!appState.equals("initialState"))
                        onCancel();
                    break;
                case "END":
                    Log.d("appState", appState);
                    if(!appState.equals("initialState"))
                        onReset();
                    break;
                case "CHANGE_DESTINATION":
                    modifyFromServer();
                    break;

            }
        }
    };

    public void modifyFromServer(){
        getTripFromServer();

    }

    public static void onReset(){
        tripState = "localTrip";
        Markers.clear();
        tripToBe = new Trip();
        DestinationCount=0;
        confirmRide.setClickable(true);
        recyclerView.getAdapter().notifyDataSetChanged();
       // Log.d("dodo", "size:" + chosenMarkerArrayList.size());
        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
          //  Log.d("dodo", chosenMarkerArrayList.get(i).getTitle());
            markerView.setImageResource(R.drawable.ic_marker_black);
            markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
            chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
            //chosenMarkerArrayList.remove(i);
          //  Log.d("dodo", "size gowa:" + chosenMarkerArrayList.size());
        }
        chosenMarkerArrayList.clear();
//                if(chosenMarkerArrayList.get(0) != null){
//                    markerView.setImageResource(R.drawable.ic_marker_black);
//                    markerText.setText(MapsFragment.chosenMarkerArrayList.get(0).getTitle());
//                    chosenMarkerArrayList.get(0).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
//                    chosenMarkerArrayList.remove(0);
//                }

        BottomSheetText.setText("Pick a drop-off location");
        BottomSheetText.setAlpha((float) 0.54);
        //notifyItemRemoved(holder.getAdapterPosition());
        appState = "initialState";
        removePolylines();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet2.setVisibility(View.GONE);


    }

    private BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            java.lang.String status = bundle.getString("EVENT");
            if(status.equals("ResetAll")) {
                if(!appState.equals("initialState"))
                    onReset();
            }
            if(status.equals("Modify")) {
              onModify();
            }
            if(status.equals("DestinationArrived")){

                onDestinationArrived();

            }
            if(status.equals("FinalDestinationArrived")){

                onFinalDestinationArrived();

            }
            }



    };

    private void onFinalDestinationArrived() {
        destCard.setVisibility(View.VISIBLE);

        destCradBtn.setText("Done");
        destCardText.setText("Final Destination Arrived ");
        bottomSheet2.setVisibility(View.GONE);
        Button Continue=(Button) view.findViewById(R.id.Continue);
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destCard.setVisibility(View.GONE);
                onReset();
                getActivity().onBackPressed();
//                destCard.setVisibility(View.GONE);
//                bottomSheet2.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        CheckInternet();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                CheckGPS();
//                // Do something after 5s = 5000ms
//                //buttons[inew][jnew].setBackgroundColor(Color.BLACK);
//            }
//        }, 5000);

    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        // ...
        super.onSaveInstanceState(outState);
    }

    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Permission Missing")
                        .setMessage("Please give the missing permissions for the app to function")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestPermissions(new java.lang.String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create().show();
            } else {
                requestPermissions(new java.lang.String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull java.lang.String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getContext(), "Please provide location permission", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        LatLng Admission = new LatLng(29.988428, 31.4389311);
        final LatLng B = new LatLng(29.9859256, 31.4386074);
        final LatLng C = new LatLng(29.9859929, 31.4392198);
        if(isInternetAvailable() && !markersRequested) {
            getMarkersFromServer();
            markersRequested=true;
        }




//        pinLocations.put("Admission Building", new LatLng(29.988428, 31.4389311));
//        pinLocations.put("B Building", new LatLng(29.9859256, 31.4386074));
//        pinLocations.put("C Building", new LatLng(29.9859929, 31.4392198));
//        pinLocations.put("D Building", new LatLng(29.9870481, 31.4410851));
//        pinLocations.put("Parking", new LatLng(29.985347, 31.440862));

//        mMap.addMarker(new MarkerOptions().position(Admission).title("Admission Building"));
//        mMap.addMarker(new MarkerOptions().position(B).title("B Building"));
//        mMap.addMarker(new MarkerOptions().position(C).title("C Building"));
       // Log.d("osamaa", java.lang.String.valueOf(pinLocations.size()));
//        for(String key : pinLocations.keySet()){
//            markerText=markerIcon.findViewById(R.id.Markertxt);
//            markerText.setText(key);
//            mMap.addMarker(new MarkerOptions().position(pinLocations.get(key)).title(key).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon))));
//         }
        BottomSheetText = getActivity().findViewById(R.id.WhichStop);
        BottomSheetText.setText("Pick a drop-off location");
        BottomSheetText.setAlpha((float) 0.54);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMinZoomPreference(16);
        mMap.setLatLngBoundsForCameraTarget(GUC_BOUNDS);
        LatLng latLng = new LatLng(29.9867788, 31.441697);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public boolean onMarkerClick(final Marker marker) {
//        if(!Markers.contains(marker.getTitle())) {
//            if (DestinationCount < 4) {
//                Drawable drawable = null;
//                Markers.add(marker.getTitle());
//                chosenMarkerArrayList.add(marker);
//                Log.d("brownies", String.valueOf(Markers.size()));
//                //GetRoutToMarker(marker.getPosition());
//
//                 markerView = (ImageView) markerIcon.findViewById(R.id.icon1);
//                markerView.setImageResource(R.drawable.ic_marker_blue);
//                markerText.setText(marker.getTitle());
//                marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon)));
//
//
//                BottomSheetText.setText(marker.getTitle());
//                BottomSheetText.setAlpha((float) 0.87);
//                DestinationCount++;
//            } else {
//                Toast.makeText(getContext(), "Maximum destinations reached", Toast.LENGTH_SHORT).show();
//            }
//        }else{
//            Toast.makeText(getContext(), "Duplicate destinations", Toast.LENGTH_SHORT).show();
//        }
//
//
//                return true;
//            }
//        });


        Button Start = (Button) getActivity().findViewById(R.id.start);
        Start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                edit.setVisibility(View.VISIBLE);
                Canceltrip.setVisibility(View.VISIBLE);

                if (mLastLocation != null) {
                    CheckInternet();
                    if (isInternetAvailable()) {
                        if (isGpsAvailable(getContext())) {
                                if (Markers.size() > 0) {

                                    if(appState.equals("modify"))
                                    {
                                        if(!modifyDestinations.get(modifyDestinations.size()-1).isArrived()) {
                                            edit.setVisibility(View.VISIBLE);
                                            Canceltrip.setVisibility(View.VISIBLE);

                                            setBottomSheets();
                                            bottomSheet.setVisibility(View.GONE);
                                            bottomSheet2.setVisibility(View.VISIBLE);
                                            if (appState.equals("initialState"))
                                                appState = "routeReady";

                                            if (!(VisualizationActivity.currentDestination() >= chosenMarkerArrayList.size())) {
                                                GetRoutToMarker(chosenMarkerArrayList.get(VisualizationActivity.currentDestination()).getPosition());
                                            }
                                        }
                                        else{
                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                                            builder2.setMessage("Are you sure you want to confirm your modifications? Your trip will be cancelled.");
                                            builder2.setCancelable(true);

                                            builder2.setPositiveButton(
                                                    "Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            bottomSheet.setVisibility(View.GONE);
                                                            onFinalDestinationArrived();
                                                            dialog.cancel();
                                                        }
                                                    });

                                            builder2.setNegativeButton(
                                                    "No",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });

                                            AlertDialog alert2 = builder2.create();
                                            alert2.show();
                                        }

                                    }else {
                                        GetRoutToMarker(pinLocations.get(Markers.get(0)));
                                        setBottomSheets();
                                        bottomSheet.setVisibility(View.GONE);
                                        bottomSheet2.setVisibility(View.VISIBLE);
                                        if (appState.equals("initialState"))
                                            appState = "routeReady";
                                    }


                                    //this can't be from tripToBe as it is still null





                                } else {
                                    Toast.makeText(getContext(), "Please choose a destination", Toast.LENGTH_SHORT).show();
                                }

                        }


                    }
                }else{
                    Log.d("gpssss","");
                    CheckGPS();
                    createLocationRequest();
                    startLocationUpdates();

                    //Toast.makeText(getContext(), "Location service failed..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirmRide = (Button) getActivity().findViewById(R.id.confirm_ride);
        confirmRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmRide.setClickable(false);
                final Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);

                switch (tripState){
                    case "localTrip":
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Are you sure you want to start your trip?");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        ImageView image = new ImageView(getContext());
                                        image.setImageResource(R.drawable.emergency_button2);


                                        AlertDialog.Builder builder5 = new AlertDialog.Builder(getActivity());
                                        builder5.setView(image);
                                       // builder5.setTitle("In case you feel unsafe during the trip, please press the emergency button");
                                        //builder5.setMessage(" ");
                                        builder5.setCancelable(true);
//IN CASE YOU FEEL UNSAFE DURING THE TRIP, PLEASE PRESS THE EMERGENCY BUTTON.
                                        builder5.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {


                                                        dialog.cancel();
                                                        createTrip();
                                                        std_msgs.String tr = Talker.destPublisher.newMessage();
                                                        tr.setData(Markers.get(0));
                                                        Talker.destPublisher.publish(tr);
                                                    }
                                                });
                                        AlertDialog alert15 = builder5.create();
                                        alert15.show();
//
//                                        dialog.cancel();
//                                        createTrip();
                                    }
                                });

                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                        break;

                    case "modify":

                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                        builder2.setMessage("Are you sure you want to confirm your modifications?");
                        builder2.setCancelable(true);

                        builder2.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        confirmModify();
                                        std_msgs.String tr = Talker.destPublisher.newMessage();
                                        tr.setData(getGucPlaceByLatLng(tripToBe.getDestinations().get(VisualizationActivity.currentDestination()).getLocation())
                                                );
                                        Talker.destPublisher.publish(tr);
                                        if(!localTrip){
                                            notifyServerOnContinueTrip();
                                        }
                                    }
                                });

                        builder2.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert2 = builder2.create();
                        alert2.show();



                        break;
                    case "remoteTrip":

                        AlertDialog.Builder builder3 = new AlertDialog.Builder(getActivity());
                        builder3.setMessage("Are you sure you want to start your trip?");
                        builder3.setCancelable(true);

                        builder3.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        std_msgs.String tr = Talker.destPublisher.newMessage();
                                        tr.setData(getGucPlaceByLatLng(tripToBe.getDestinations().get(0).getLocation())
                                        );
                                        Talker.destPublisher.publish(tr);
                                        notifyServerOnStartTrip();
                                        startActivity(visualizationIntent);
                                        getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
                                    }
                                });

                        builder3.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert13 = builder3.create();
                        alert13.show();

//                       onArrivePickup();

                        break;
                    case "DestinationArrived":

                        AlertDialog.Builder builder4 = new AlertDialog.Builder(getActivity());
                        builder4.setMessage("Are you sure you want to start your trip?");
                        builder4.setCancelable(true);

                        builder4.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        appState="DestinationArrived";
                                        notifyServerOnContinueTrip();
                                        std_msgs.String tr = Talker.destPublisher.newMessage();
                                        tr.setData(getGucPlaceByLatLng(tripToBe.getDestinations().get(VisualizationActivity.currentDestination()).getLocation())
                                        );
                                        Talker.destPublisher.publish(tr);
                                        startActivity(visualizationIntent);
                                        getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
                                    }
                                });

                        builder4.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert14 = builder4.create();
                        alert14.show();
                        break;
                }
            }
        });

        Canceltrip = (Button) getActivity().findViewById(R.id.Cancel);
        Canceltrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage("Are you sure you want to cancel your trip?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                onReset();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        edit = (Button) getActivity().findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(appState.equals("routeReady")){
                    getActivity().onBackPressed();
                }else{
                    onModify2();
                }

            }
        });

        createLocationRequest();
        startLocationUpdates();


    }

    public void onArrivePickup()
    {
        java.lang.String pinUrl = "https://sdc-api-gateway.herokuapp.com/car/trip/arrive/pickup/hadwa";
        // Log.d("osamaa", "I entered1");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Method.GET, pinUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }
    public void notifyServerOnStartTrip(){

        java.lang.String pinUrl = "https://sdc-api-gateway.herokuapp.com/car/trip/start/tablet/hadwa";
        // Log.d("osamaa", "I entered1");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Method.GET, pinUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    public void setBottomSheets(){



            destCard = (CardView) view.findViewById(R.id.destCard);
            TextView dest1 = (TextView) view.findViewById(R.id.dest1);
            TextView dest2 = (TextView) view.findViewById(R.id.dest2);
            TextView dest3 = (TextView) view.findViewById(R.id.dest3);

            ImageView destI1 = (ImageView) view.findViewById(R.id.dest_i1);
            ImageView destI2 = (ImageView) view.findViewById(R.id.dest_i2);
            ImageView destI3 = (ImageView) view.findViewById(R.id.dest_i3);


            ImageView destIcon1 = (ImageView) view.findViewById(R.id.dest_icon1);
            ImageView destIcon2 = (ImageView) view.findViewById(R.id.dest_icon2);

            LinearLayout destC1 = (LinearLayout) view.findViewById(R.id.dest_c1);
            LinearLayout destC2 = (LinearLayout) view.findViewById(R.id.dest_c2);
            LinearLayout destC3 = (LinearLayout) view.findViewById(R.id.dest_c3);

//            dest1.setVisibility(View.GONE);
//            dest2.setVisibility(View.GONE);
//            dest3.setVisibility(View.GONE);

            destIcon1.setVisibility(View.GONE);
            destIcon2.setVisibility(View.GONE);

//            destI1.setVisibility(View.GONE);
//            destI2.setVisibility(View.GONE);
//            destI3.setVisibility(View.GONE);

            destC1.setVisibility(View.GONE);
            destC2.setVisibility(View.GONE);
            destC3.setVisibility(View.GONE);

        Log.d("zeftState", appState);
        if(appState.equals("DestinationArrived") || appState.equals("modify")) {
            int zeftSize = 0;
            //Log.d("zeftState", appState);
            if(appState.equals("DestinationArrived")) {
                zeftMarkers.clear();
                //ArrayList<java.lang.String> zeftMarkers = new ArrayList<>();
                for (int i = 0; i < tripToBe.getDestinations().size(); i++) {
                    if (!tripToBe.getDestinations().get(i).isArrived()) {
                        //Log.d("zeft", getGucPlaceByLatLng(tripToBe.getDestinations().get(i).getLocation()));
                        zeftSize++;
                        zeftMarkers.add(getGucPlaceByLatLng(tripToBe.getDestinations().get(i).getLocation()));
                    }
                }
            }else{

                zeftMarkers.clear();
                for(int i = 0; i<modifyDestinations.size();i++){
                    if(!modifyDestinations.get(i).isArrived())
                        zeftMarkers.add(getGucPlaceByLatLng(modifyDestinations.get(i).getLocation()));
                }
                zeftSize = zeftMarkers.size();
            }
            Log.d("zeftsize", java.lang.String.valueOf(zeftSize));

            if (zeftSize == 1) {
                dest1.setText(zeftMarkers.get(0));
//                dest1.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
                destC1.setVisibility(View.VISIBLE);
            }
            if (zeftSize == 2) {
                dest1.setText(zeftMarkers.get(0));
                dest2.setText(zeftMarkers.get(1));
                destC1.setVisibility(View.VISIBLE);
                destC2.setVisibility(View.VISIBLE);
//                dest1.setVisibility(View.VISIBLE);
//                dest2.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
//                destI2.setVisibility(View.VISIBLE);
                destIcon1.setVisibility(View.VISIBLE);
            }
            if (zeftSize == 3) {
                dest1.setText(zeftMarkers.get(0));
                dest2.setText(zeftMarkers.get(1));
                dest3.setText(zeftMarkers.get(2));
                destC1.setVisibility(View.VISIBLE);
                destC2.setVisibility(View.VISIBLE);
                destC3.setVisibility(View.VISIBLE);
//                dest1.setVisibility(View.VISIBLE);
//                dest2.setVisibility(View.VISIBLE);
//                dest3.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
//                destI2.setVisibility(View.VISIBLE);
//                destI3.setVisibility(View.VISIBLE);
                destIcon1.setVisibility(View.VISIBLE);
                destIcon2.setVisibility(View.VISIBLE);
            }
        }else{
            if (Markers.size() == 1) {
                dest1.setText(Markers.get(0));
                destC1.setVisibility(View.VISIBLE);
//                dest1.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
            }
            if (Markers.size() == 2) {
                dest1.setText(Markers.get(0));
                dest2.setText(Markers.get(1));
                destC1.setVisibility(View.VISIBLE);
                destC2.setVisibility(View.VISIBLE);
//                dest1.setVisibility(View.VISIBLE);
//                dest2.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
//                destI2.setVisibility(View.VISIBLE);
                destIcon1.setVisibility(View.VISIBLE);
            }
            if (Markers.size() == 3) {
                dest1.setText(Markers.get(0));
                dest2.setText(Markers.get(1));
                dest3.setText(Markers.get(2));
                destC1.setVisibility(View.VISIBLE);
                destC2.setVisibility(View.VISIBLE);
                destC3.setVisibility(View.VISIBLE);
//                dest1.setVisibility(View.VISIBLE);
//                dest2.setVisibility(View.VISIBLE);
//                dest3.setVisibility(View.VISIBLE);
//                destI1.setVisibility(View.VISIBLE);
//                destI2.setVisibility(View.VISIBLE);
//                destI3.setVisibility(View.VISIBLE);
                destIcon1.setVisibility(View.VISIBLE);
                destIcon2.setVisibility(View.VISIBLE);
            }

        }
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void GetRoutToMarker(LatLng clickedMarker) {
        //ClickM = clickedMarker;
        GoogleDirection.withServerKey("AIzaSyCZ5TH2mfl26LqDq6kkVbo85gLPZ9fmaik")
                .from(mLastLocation)
                .to(clickedMarker)
                .transportMode(TransportMode.DRIVING)
                .execute(this);

    }

    public void onModify2(){
        modifyDestinations = new ArrayList<>();
        modifyDestinations = tripToBe.getDestinations();

        modifyState = "modifyEdit";
        tripState = "modify";
        appState = "modify";


        removePolylines();
        recyclerView.getAdapter().notifyDataSetChanged();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet2.setVisibility(View.GONE);

//        for (int i = 0; i< tripToBe.getDestinations().size(); i++){
//            if(!tripToBe.getDestinations().get(i).isArrived())
//                chosenMarkerArrayList.add(GetMarkersFromLatlng(tripToBe.getDestinations().get(i).getLocation()));
//        }
//        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
//            markerView.setImageResource(R.drawable.ic_marker_blue);
//            markerText.setText(chosenMarkerArrayList.get(i).getTitle());
//            Log.d("Marker Titles", chosenMarkerArrayList.get(i).getTitle() + Markers.size());
//            chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
//            Log.d("Marker Titles2", MapsFragment.chosenMarkerArrayList.get(i).getTitle());
//            Markers.add(chosenMarkerArrayList.get(i).getTitle());
//        }
    }
    public void onModify(){
//        Log.d("FirstMarker",Markers.get(0));
        modifyDestinations = new ArrayList<>();
        modifyDestinations = tripToBe.getDestinations();
        Markers.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
        DestinationCount =0;
        tripState = "modify";
        modifyState = "modifyModify";

//        for (int i = 0; i<Markers.size(); i++){
//            Markers.remove(i);
//        }
       // Log.d("FirstMarker",Markers.get(0));
        Log.d("FirstMarker",Markers.size()+"");
//        recyclerView.notifyAll();
        removePolylines();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet2.setVisibility(View.GONE);
        //confirmRide.setClickable(true);
        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
            markerView.setImageResource(R.drawable.ic_marker_black);
            markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
            chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));

        }
        chosenMarkerArrayList.clear();


        appState = "modify";
        for (int i = 0; i< tripToBe.getDestinations().size(); i++){
           // if(!tripToBe.getDestinations().get(i).isArrived())
                chosenMarkerArrayList.add(GetMarkersFromLatlng(tripToBe.getDestinations().get(i).getLocation()));
                DestinationCount++;
        }
        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
            if(!tripToBe.getDestinations().get(i).isArrived()) {
                markerView.setImageResource(R.drawable.ic_marker_blue);
                markerText.setText(chosenMarkerArrayList.get(i).getTitle());
                Log.d("Marker Titles", chosenMarkerArrayList.get(i).getTitle() + Markers.size());
                chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
                Log.d("Marker Titles2", MapsFragment.chosenMarkerArrayList.get(i).getTitle());
            }
            Markers.add(chosenMarkerArrayList.get(i).getTitle());
        }
    }

    public void onDestinationArrived(){
        destCradBtn.setText("Continue");
        destCardText.setText(" Destination Arrived ");
        destCard.setVisibility(View.VISIBLE);
        bottomSheet2.setVisibility(View.GONE);
        Button Continue=(Button) view.findViewById(R.id.Continue);
        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                destCard.setVisibility(View.GONE);
                bottomSheet2.setVisibility(View.VISIBLE);
            }
        });
        Markers.clear();
        appState= "DestinationArrived";
        removePolylines();
        edit.setVisibility(View.VISIBLE);
        Canceltrip.setVisibility(View.VISIBLE);
        //confirmRide.setClickable(true);
        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
            if(tripToBe.getDestinations().get(i).isArrived()) {
                //munf3sh ashan myadsh tany destinations
                //DestinationCount--;
                markerView.setImageResource(R.drawable.ic_marker_black);
                markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
                chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
            }
        }
        for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
            Markers.add(chosenMarkerArrayList.get(i).getTitle());
            if(!tripToBe.getDestinations().get(i).isArrived()) {
                markerView.setImageResource(R.drawable.ic_marker_blue);
                markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
                chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
                Log.d("FirstMarker1",""+tripToBe.getDestinations().get(i).getLocation());
                Log.d("FirstMarker2",""+ chosenMarkerArrayList.get(i));
//                Markers.add(chosenMarkerArrayList.get(i).getTitle());

            }
        }
        //TODO #5
        GetRoutToMarker(tripToBe.getDestinations().get(VisualizationActivity.currentDestination()).getLocation());

        //chosenMarkerArrayList.clear();
        setBottomSheets();
        bottomSheet.setVisibility(View.GONE);
        //bottomSheet2.setVisibility(View.VISIBLE);
        tripState = "DestinationArrived";



    }

    public Marker GetMarkersFromLatlng(LatLng latlng){
        for (int i =0 ; i<AllMarkersArrayList.size();i++){
          //  Log.d("getMarkerFromLatLng", AllMarkersArrayList.get(i).getPosition().toString());
           // Log.d("getMarkerFromLatLng", "latln"+latlng.toString());

            if(AllMarkersArrayList.get(i).getPosition().equals(latlng))
            {
                Log.d("getMarkerFromLatLng", "did I come here?");
                return AllMarkersArrayList.get(i);
            }
            Log.d("getMarkerFromLatLng", "did I come here tayb??");

        }
        return null;
    }

    public boolean Arrived(int i){


        return tripToBe.getDestinations().get(i).isArrived();
    }


    /*public void onRoutingSuccess(ArrayList<Route> route, int j) {

            if (polylines.size() > 0) {
                for (Polyline poly : polylines) {
                    poly.remove();
                }

        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

*/
    @Override
    public void onClick(View v) {


    }


    @Override
    public void onDirectionSuccess(Direction direction, java.lang.String rawBody) {
       // Log.d("Polylines", direction.getStatus());

        if (direction.isOK()) {
        //    Log.d("Polylines", "eh b2aa");
         //   Log.d("Polylines", java.lang.String.valueOf(direction.getRouteList().get(0)));
            Route route = direction.getRouteList().get(0);
            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            //mMap.addPolyline(DirectionConverter.createPolyline(getContext(), directionPositionList, 4, Color.BLACK));
//            polylines = new ArrayList<>();
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLACK);
            polyOptions.width(6);
            polyOptions.addAll(directionPositionList);
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            setCameraWithCoordinationBounds(route);
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void CheckInternet() {
        if (!isInternetAvailable()) {
            Alerter.create(getActivity())
                    .setTitle("No internet connection")
                    .setText("Tap to retry")
                    .setBackgroundColorInt(Color.RED)
                    .enableInfiniteDuration(true)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isInternetAvailable()) {
                                Alerter.hide();
                                if(!markersRequested) {
                                    getMarkersFromServer();
                                    markersRequested = true;
                                }
                            }
                        }
                    })
                    .setOnShowListener(new OnShowAlertListener() {
                        @Override
                        public void onShow() {
                            if (isInternetAvailable()) {
                                Alerter.hide();
                            }
                        }
                    })
                    .show();
        }
    }

    public void CheckGPS() {
        if (!isGpsAvailable(getContext())) {
            Alerter.create(getActivity())
                    .setTitle("Location is turned off!")
                    .setText("Please check your location settings")
                    .setBackgroundColorInt(Color.RED)
                    .enableInfiniteDuration(true)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })

                    .show();

        } else if (mLastLocation == null) {
            Alerter.create(getActivity())
                    .setTitle("Can not get location updates")
                    .setText("Please check your location settings")
                    .setBackgroundColorInt(Color.RED)
                    .setDuration(5000)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })

                    .show();
        }

    }

    public boolean isGpsAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        //Log.d("markercb", "ana geet hena");
        if (appState == "initialState" || appState == "modify") {
            if (!Markers.contains(marker.getTitle())) {
                if (DestinationCount < 3) {
                    Drawable drawable = null;
                    Markers.add(marker.getTitle());
                    chosenMarkerArrayList.add(marker);
                    if(chosenMarkerArrayList.size()==1)
                    {
                        setCarAvailability(false);
                    }
                  //  Log.d("brownies", java.lang.String.valueOf(Markers.size()));
                    //GetRoutToMarker(marker.getPosition());
//                    markerView = (ImageView) markerIcon.findViewById(R.id.icon1);
                    markerView.setImageResource(R.drawable.ic_marker_blue);
                    markerText.setText(marker.getTitle());
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon)));


                    BottomSheetText.setText(marker.getTitle());
                    BottomSheetText.setAlpha((float) 0.87);


                    if(appState.equals("modify")) {
                        Log.d("modDest", DestinationCount +" Mod "+ modifyDestinations.size());
                        for (int i = 0; i < DestinationCount; i++) {
                            if (getGucPlaceByLatLng(modifyDestinations.get(i).getLocation()) != marker.getTitle())
                            {
                                tmam=true;
                            }else{
                                tmam=false;
                                break;
                            }
                        }
                        if(tmam) {
                            modifyDestinations.add(new TripDestination(marker.getPosition()));
                            DestinationCount++;
                        }

                    }
                    if(appState == "initialState")
                        DestinationCount++;
                } else {
                    Toast.makeText(getContext(), "Maximum destinations reached", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Duplicate destinations", Toast.LENGTH_SHORT).show();
            }

        }
//        if(appState.equals("modify")) {
//            Log.d("modDest", DestinationCount +" Mod "+ modifyDestinations.size());
//            for (int i = 0; i < DestinationCount; i++) {
//                if (getGucPlaceByLatLng(modifyDestinations.get(i).getLocation()) != marker.getTitle())
//                {
//                    tmam=true;
//                }else{
//                    tmam=false;
//                    break;
//                }
//            }
//            if(tmam)
//            modifyDestinations.add(new TripDestination(marker.getPosition()));
//
//        }
        return true;
    }

    public static void removePolylines() {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLastLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private void getMarkersFromServer() {
        java.lang.String pinUrl = "https://sdc-api-gateway.herokuapp.com/guc/pins";
       // Log.d("osamaa", "I entered1");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Method.GET, pinUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject responseObject = response.getJSONObject(i);
                            //    Log.d("osamaa", responseObject.get("name").toString());
                             //   Log.d("osamaa", java.lang.String.valueOf(new LatLng(responseObject.getJSONObject("latLng").getDouble("latitude"), responseObject.getJSONObject("latLng").getDouble("longitude"))));
                                pinLocations.put(responseObject.getString("name"), new LatLng(responseObject.getJSONObject("latLng").getDouble("latitude"), responseObject.getJSONObject("latLng").getDouble("longitude")));
                                // Log.d("blaaa", pinLocations.toString() );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        for (java.lang.String key : pinLocations.keySet()) {
                            markerText = markerIcon.findViewById(R.id.Markertxt);
                            markerText.setText(key);

                            AllMarkersArrayList.add( mMap.addMarker(new MarkerOptions().position(pinLocations.get(key)).title(key).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon)))));
                        }
//                        drawPins(gucPlaces);
//                        MainActivity.gucPlaces = gucPlaces;
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);

    }
    public static java.lang.String getGucPlaceByLatLng(LatLng latLng){
        for(Object place : MapsFragment.pinLocations.keySet()){
            if(MapsFragment.pinLocations.get(place).equals(latLng)){
                //    Log.d("mo7eyy",place.toString());
                return (java.lang.String) place;

            }


        }
        return null;

    }
    // TODO: Get pickup location from server, and send to car
    // TODO: Send to server a POST request when I reach pickup location
    private void getTripFromServer() {
        localTrip=false;
        Markers.clear();
//        chosenMarkerArrayList.clear();
        tripState = "remoteTrip";
        java.lang.String tripUrl = "https://sdc-api-gateway.herokuapp.com/car/find/hadwa";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, tripUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject obj = response;
                Car car = gson.fromJson(obj.toString(), Car.class);
                if(car.getCurrentTrip()!=null){
                     tripToBe = car.getCurrentTrip();

                    for(int i = 0; i< tripToBe.getDestinations().size(); i++){
                        TripDestination d = tripToBe.getDestinations().get(i);
                        chosenMarkerArrayList.add(GetMarkersFromLatlng(tripToBe.getDestinations().get(i).getLocation()));
                        Markers.add(getGucPlaceByLatLng(d.getLocation()));
                    }
                    for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
                        if (!tripToBe.getDestinations().get(i).isArrived()) {
                            markerView.setImageResource(R.drawable.ic_marker_blue);
                            markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
                            chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));

                        }
                    }

                    DestinationCount = Markers.size();


                    if (mLastLocation != null) {
                        CheckInternet();
                        if (isInternetAvailable()) {
                            if (isGpsAvailable(getContext())) {
                                if (Markers.size() > 0) {
//                        if(appState.equals("routeReady")) {
//                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                @Override
//                                public boolean onMarkerClick(Marker marker) {
//                                    return true;
//                                }
//                            });
//                        }
                                    appState = "routeReady";
                                    //TODO #4
                                    GetRoutToMarker(tripToBe.getDestinations().get(VisualizationActivity.currentDestination()).getLocation());
                                    bottomSheet = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout);
                                    bottomSheet2 = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout2);

                                    TextView dest1 = (TextView) getActivity().findViewById(R.id.dest1);
                                    TextView dest2 = (TextView) getActivity().findViewById(R.id.dest2);
                                    TextView dest3 = (TextView) getActivity().findViewById(R.id.dest3);

                                    ImageView destIcon1 = (ImageView) getActivity().findViewById(R.id.dest_icon1);
                                    ImageView destIcon2 = (ImageView) getActivity().findViewById(R.id.dest_icon2);

                                    ImageView destI1 = (ImageView) getActivity().findViewById(R.id.dest_i1);
                                    ImageView destI2 = (ImageView) getActivity().findViewById(R.id.dest_i2);
                                    ImageView destI3 = (ImageView) getActivity().findViewById(R.id.dest_i3);

                                    LinearLayout destC1 = (LinearLayout) getActivity().findViewById(R.id.dest_c1);
                                    LinearLayout destC2 = (LinearLayout) getActivity().findViewById(R.id.dest_c2);
                                    LinearLayout destC3 = (LinearLayout) getActivity().findViewById(R.id.dest_c3);
                                    //===========TextView bta3 el destinations====
//                                    dest1.setVisibility(View.GONE);
//                                    dest2.setVisibility(View.GONE);
//                                    dest3.setVisibility(View.GONE);
                                    //==============el fawasel el fl nos==========
                                    destIcon1.setVisibility(View.GONE);
                                    destIcon2.setVisibility(View.GONE);
                                    //================el blue markers=============
//                                    destI1.setVisibility(View.GONE);
//                                    destI2.setVisibility(View.GONE);
//                                    destI3.setVisibility(View.GONE);

                                    destC1.setVisibility(View.GONE);
                                    destC2.setVisibility(View.GONE);
                                    destC3.setVisibility(View.GONE);


                                    if (Markers.size() == 1) {
                                        dest1.setText(Markers.get(0));
                                        destC1.setVisibility(View.VISIBLE);
//                                        dest1.setVisibility(View.VISIBLE);
//                                        destI1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 2) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        destC1.setVisibility(View.VISIBLE);
                                        destC2.setVisibility(View.VISIBLE);
//                                        dest1.setVisibility(View.VISIBLE);
//                                        dest2.setVisibility(View.VISIBLE);
//                                        destI1.setVisibility(View.VISIBLE);
//                                        destI2.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 3) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest3.setText(Markers.get(2));
                                        destC1.setVisibility(View.VISIBLE);
                                        destC2.setVisibility(View.VISIBLE);
                                        destC3.setVisibility(View.VISIBLE);
//                                        dest1.setVisibility(View.VISIBLE);
//                                        dest2.setVisibility(View.VISIBLE);
//                                        dest3.setVisibility(View.VISIBLE);
//                                        destI1.setVisibility(View.VISIBLE);
//                                        destI2.setVisibility(View.VISIBLE);
//                                        destI3.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                        destIcon2.setVisibility(View.VISIBLE);
                                    }


                                    bottomSheet.setVisibility(View.GONE);
                                    bottomSheet2.setVisibility(View.VISIBLE);

                                    onArrivePickup();
                                } else {
                                    Toast.makeText(getContext(), "Please choose a destination", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }
                    }else{
                        createLocationRequest();
                        startLocationUpdates();
                        CheckGPS();
                        //Toast.makeText(getContext(), "Location service failed..", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error

            }
        });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    public Marker LatLngToMarker(){

        return null;
    }

    public void confirmModify(){
        final java.lang.String modifyTripUrl = "https://sdc-api-gateway.herokuapp.com/car/trip/change/tablet/hadwa";
        Trip modifyTrip= new Trip();
        List<TripDestination> newDestinations = new ArrayList<>();
        //ArrayList<LatLng> destinationList = new ArrayList<LatLng>();
        for(int i =0; i<chosenMarkerArrayList.size(); i++){
            newDestinations.add(new TripDestination(chosenMarkerArrayList.get(i).getPosition()));

        }
        for (int i=0 ; i < tripToBe.getDestinations().size() ; i++){
            if(tripToBe.getDestinations().get(i).isArrived()){
                newDestinations.get(i).setArrived(true);
            }
        }
        //TODO this needs to be in start button

            int tripsLeft=0;
        for (int i=0 ; i < newDestinations.size() ; i++){
            if(!newDestinations.get(i).isArrived()){
                tripsLeft++;
            }
        }
        Log.d("TripsLeft", java.lang.String.valueOf(tripsLeft));
        if(tripsLeft==0){
            confirmRide.setClickable(true);  //I added this to make confirmRide clickable if the ride was cancelled because of a modification.
            onReset();
            tripUrl = "https://sdc-api-gateway.herokuapp.com/car/trip/arrive/final/hadwa";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, tripUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error

                }
            });
            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        }else {
            modifyTrip.setDestinations(newDestinations);
            java.lang.String str = gson.toJson(modifyTrip);
            JSONObject trip = new JSONObject();
            tripToBe.setDestinations(newDestinations);
            try {
                trip = new JSONObject(str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest tripRequest = new JsonObjectRequest(Method.POST, modifyTripUrl, trip, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //createdTrip = gson.fromJson(response.toString(),Trip.class);
                    //tripToBe = createdTrip;
//                Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
//                startActivity(visualizationIntent);
//                getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ErrorModify", java.lang.String.valueOf(error.networkResponse.statusCode));
                }
            });

            MySingleton.getInstance(getContext()).addToRequestQueue(tripRequest);

            Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
            startActivity(visualizationIntent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
        }

    }




    public void createTrip(){
        final java.lang.String createTripUrl = "https://sdc-api-gateway.herokuapp.com/car/create/trip";
        if(mLastLocation!=null) {
            requestTrip.setPickupLocation(mLastLocation);
        }
        ArrayList<LatLng> destinationList = new ArrayList<LatLng>();
        for(int i =0; i<chosenMarkerArrayList.size(); i++){
            destinationList.add(chosenMarkerArrayList.get(i).getPosition());
        }
        requestTrip.setDestinations(destinationList);
        requestTrip.setCarID("hadwa");
        //requestTrip.setTabletFcmToken(MyFirebaseInstanceIDService.getToken());
      //  Log.d("trip-server-car", requestTrip.getCarID());
        java.lang.String str = gson.toJson(Trip.toTrip(requestTrip));
        JSONObject trip = new JSONObject();

        try {
            trip = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
      //  Log.d("trip-server", trip.toString());
        JsonObjectRequest tripRequest = new JsonObjectRequest(Method.POST, createTripUrl, trip, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                createdTrip = gson.fromJson(response.toString(),Trip.class);
                tripToBe = createdTrip;
                Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
                //Bundle bundle = new Bundle();
                //bundle.putParcelable("destination", (Parcelable) createdTrip);
                //visualizationIntent.putExtra("destination", createdTrip);
                //visualizationIntent.putExtra("bundle", bundle);
                //visualizationIntent.putExtra("destination", pinLocations.get(Markers.get(0)));
                startActivity(visualizationIntent);
                getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(tripRequest);
    }

    public void startTripFromServer(){
        Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
        //Bundle bundle = new Bundle();
        //bundle.putParcelable("trip", (Parcelable) trip);
        //visualizationIntent.putExtra("bundle", bundle);
        //visualizationIntent.putExtra("destination", pinLocations.get(Markers.get(0)));
        startActivity(visualizationIntent);
        getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
    }
    public void onCancel(){

       onReset();

    }


    public void updateFCMServer(){
        Car car = new Car();
        car.setcarID("hadwa");
        car.setTabletFcmToken(MyFirebaseInstanceIDService.getToken());

        java.lang.String newTokenUrl = "https://sdc-api-gateway.herokuapp.com/car/admin/token/tablet";
        java.lang.String str = gson.toJson(car);
        JSONObject carJSON = new JSONObject();
        try {
            carJSON = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest tripRequest = new JsonObjectRequest(Method.POST, newTokenUrl, carJSON, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(tripRequest);

    }

    public void notifyServerOnContinueTrip(){
        java.lang.String pinUrl = "https://sdc-api-gateway.herokuapp.com/car/trip/continue/tablet/hadwa";
        // Log.d("osamaa", "I entered1");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Method.GET, pinUrl, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    public void setCarAvailability(boolean carAvailability ){
        java.lang.String modifyTripUrl;
        if(carAvailability){
             modifyTripUrl = "https://sdc-trip-car-management.herokuapp.com/car/update/availability/hadwa/true";
        }else
            {
                modifyTripUrl = "https://sdc-trip-car-management.herokuapp.com/car/update/availability/hadwa/false";

            }

        JsonObjectRequest tripRequest = new JsonObjectRequest(Method.POST, modifyTripUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorModify", java.lang.String.valueOf(error.networkResponse.statusCode));
            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(tripRequest);


    }

    @Override
    public void onListChange() {
        //Log.d("listener", "geet hena?");
        if(chosenMarkerArrayList.size() == 0 && appState!="modify"){
            setCarAvailability(true);
        }
    }

}

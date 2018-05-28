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
import org.ros.message.MessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import std_msgs.String;

import sensor_msgs.Image;
import std_msgs.String;

import static com.github.ros_java.test_android.sensor_serial.Listener.imageSub;
import static com.github.ros_java.test_android.sensor_serial.Listener.subscriber;


//import static com.example.hadwa.myapplication.R.drawable.ic_marker_black;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener,DirectionCallback, GoogleMap.OnMarkerClickListener {

    static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    boolean mRequestingLocationUpdates;
    static LatLng mLastLocation;
    private LatLng Dest1 = new LatLng(29.988428, 31.4389311);
    static HashMap<java.lang.String, LatLng> pinLocations;
    static ArrayList<Marker> chosenMarkerArrayList;
    static TextView markerText;
    static View markerIcon;
    static java.lang.String appState;
    RequestTrip requestTrip;
    private final static java.lang.String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    public static final int REQUEST_CHECK_SETTINGS = 10;
    private static final LatLngBounds GUC_BOUNDS = new LatLngBounds(new LatLng(29.9842014, 31.4387794),
            new LatLng(29.9899635, 31.4445531));

    Trip createdTrip;
    static ImageView markerView;
    static TextView BottomSheetText;
    private View view;
    static List<java.lang.String> Markers;
    RecyclerView recyclerView;
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
        //polylines = new ArrayList<>();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        //NewText = (TextView)findViewById(R.id.WhichStop);

        tripState = "localTrip";
        requestTrip = new RequestTrip();
        createdTrip = new Trip();


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



//        BottomSheetText.setText("Pick a drop-off location");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        Markers = new ArrayList<>();
        chosenMarkerArrayList = new ArrayList<Marker>();
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

        subscriber.addMessageListener(new MessageListener<String>() {
            @Override
            public void onNewMessage(std_msgs.String s) {
                speedValue = s.getData();
                Log.d("listener-log","I heard: \"" + s.getData() + "\"");
            }
        });



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
            Log.d("eh ya status da?", status);
            switch (status) {

                case "NEW":
                    getTripFromServer();
                    break;
                case "CANCEL":
                    onCancel();
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

    private BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            java.lang.String status = bundle.getString("EVENT");
            if(status=="ResetAll") {

                Markers.clear();
                DestinationCount=0;
                Log.d("dodo", "size:" + chosenMarkerArrayList.size());
                for (int i=0 ;i<chosenMarkerArrayList.size();i++) {
                    Log.d("dodo", chosenMarkerArrayList.get(i).getTitle().toString());
                    markerView.setImageResource(R.drawable.ic_marker_black);
                    markerText.setText(MapsFragment.chosenMarkerArrayList.get(i).getTitle());
                    chosenMarkerArrayList.get(i).setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapsFragment.markerIcon.getContext(), markerIcon)));
                    //chosenMarkerArrayList.remove(i);
                    Log.d("dodo", "size gowa:" + chosenMarkerArrayList.size());
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
            }


    };
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
        Log.d("osamaa", java.lang.String.valueOf(pinLocations.size()));
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
                                    if(tripState == "remoteTrip"){
                                        List<TripDestination> newDestinations = new ArrayList<>();

                                        for(int i =0; i<Markers.size(); i++){

                                            newDestinations.add(new TripDestination(pinLocations.get(Markers.get(i))));

                                        }


                                        Trip modifiedTrip = new Trip();
                                        modifiedTrip.setDestinations(newDestinations);
                                        java.lang.String url = "https://sdc-trip-car-management.herokuapp.com/car/trip/change/tablet";
                                        java.lang.String str = gson.toJson(modifiedTrip);
                                        JSONObject trip = new JSONObject();
                                        try {
                                            trip = new JSONObject(str);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        JsonObjectRequest tripRequest = new JsonObjectRequest
                                                (Request.Method.POST, url, trip, new Response.Listener<JSONObject>() {

                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.v("EDITDESTINATION", response.toString());
                                                        Trip tripp = gson.fromJson(response.toString(), Trip.class);



                                                    }
                                                }, new Response.ErrorListener() {

                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                });

                                        MySingleton.getInstance(getContext()).addToRequestQueue(tripRequest);

                                    }
                                    appState = "routeReady";
                                    GetRoutToMarker(pinLocations.get(Markers.get(0)));
                                    bottomSheet = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout);
                                    bottomSheet2 = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout2);

                                    TextView dest1 = (TextView) getActivity().findViewById(R.id.dest1);
                                    TextView dest2 = (TextView) getActivity().findViewById(R.id.dest2);
                                    TextView dest3 = (TextView) getActivity().findViewById(R.id.dest3);
                                    TextView dest4 = (TextView) getActivity().findViewById(R.id.dest4);

                                    ImageView destIcon1 = (ImageView) getActivity().findViewById(R.id.dest_icon1);
                                    ImageView destIcon2 = (ImageView) getActivity().findViewById(R.id.dest_icon2);
                                    ImageView destIcon3 = (ImageView) getActivity().findViewById(R.id.dest_icon3);

                                    dest1.setVisibility(View.GONE);
                                    dest2.setVisibility(View.GONE);
                                    dest3.setVisibility(View.GONE);
                                    dest4.setVisibility(View.GONE);
                                    destIcon1.setVisibility(View.GONE);
                                    destIcon2.setVisibility(View.GONE);
                                    destIcon3.setVisibility(View.GONE);

                                    if (Markers.size() == 1) {
                                        dest1.setText(Markers.get(0));
                                        dest1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 2) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 3) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest3.setText(Markers.get(2));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        dest3.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                        destIcon2.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 4) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest3.setText(Markers.get(2));
                                        dest4.setText(Markers.get(3));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        dest3.setVisibility(View.VISIBLE);
                                        dest4.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                        destIcon2.setVisibility(View.VISIBLE);
                                        destIcon3.setVisibility(View.VISIBLE);
                                    }


                                    bottomSheet.setVisibility(View.GONE);
                                    bottomSheet2.setVisibility(View.VISIBLE);


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
        });

        confirmRide = (Button) getActivity().findViewById(R.id.confirm_ride);
        confirmRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRide.setClickable(false);
                if(tripState == "localTrip") {
                    createTrip();

//                    Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelable("destination", pinLocations.get(Markers.get(0)));
//                    visualizationIntent.putExtra("bundle", bundle);
//                    //visualizationIntent.putExtra("destination", pinLocations.get(Markers.get(0)));
//                    startActivity(visualizationIntent);
//                    getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
                }else{
                Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("destination", pinLocations.get(Markers.get(0)));
                visualizationIntent.putExtra("bundle", bundle);
                startActivity(visualizationIntent);
                getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
                }
//                Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("destination", pinLocations.get(Markers.get(0)));
//                visualizationIntent.putExtra("bundle", bundle);
//                //visualizationIntent.putExtra("destination", pinLocations.get(Markers.get(0)));
//                startActivity(visualizationIntent);
//                getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
            }
        });

        createLocationRequest();
        startLocationUpdates();


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
        Log.d("Polylines", direction.getStatus());

        if (direction.isOK()) {
            Log.d("Polylines", "eh b2aa");
            Log.d("Polylines", java.lang.String.valueOf(direction.getRouteList().get(0)));
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
        if (appState == "initialState") {
            if (!Markers.contains(marker.getTitle())) {
                if (DestinationCount < 4) {
                    Drawable drawable = null;
                    Markers.add(marker.getTitle());
                    chosenMarkerArrayList.add(marker);
                    Log.d("brownies", java.lang.String.valueOf(Markers.size()));
                    //GetRoutToMarker(marker.getPosition());

                    markerView = (ImageView) markerIcon.findViewById(R.id.icon1);
                    markerView.setImageResource(R.drawable.ic_marker_blue);
                    markerText.setText(marker.getTitle());
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon)));


                    BottomSheetText.setText(marker.getTitle());
                    BottomSheetText.setAlpha((float) 0.87);
                    DestinationCount++;
                } else {
                    Toast.makeText(getContext(), "Maximum destinations reached", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Duplicate destinations", Toast.LENGTH_SHORT).show();
            }

        }
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
        java.lang.String pinUrl = "https://sdc-trip-car-management.herokuapp.com/guc/pins";
        Log.d("osamaa", "I entered1");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Method.GET, pinUrl, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject responseObject = response.getJSONObject(i);
                                Log.d("osamaa", responseObject.get("name").toString());
                                Log.d("osamaa", java.lang.String.valueOf(new LatLng(responseObject.getJSONObject("latLng").getDouble("latitude"), responseObject.getJSONObject("latLng").getDouble("longitude"))));
                                pinLocations.put(responseObject.getString("name"), new LatLng(responseObject.getJSONObject("latLng").getDouble("latitude"), responseObject.getJSONObject("latLng").getDouble("longitude")));
                                // Log.d("blaaa", pinLocations.toString() );

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        for (java.lang.String key : pinLocations.keySet()) {
                            markerText = markerIcon.findViewById(R.id.Markertxt);
                            markerText.setText(key);
                            mMap.addMarker(new MarkerOptions().position(pinLocations.get(key)).title(key).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getContext(), markerIcon))));
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
                    Log.d("mo7eyy",place.toString());
                return (java.lang.String) place;

            }


        }
        return null;

    }
    // TODO: Get pickup location from server, and send to car
    // TODO: Send to server a POST request when I reach pickup location
    private void getTripFromServer() {

        //TODO:Clear markers when I finish a trip.
        Markers.clear();
        tripState = "remoteState";
        java.lang.String tripUrl = "https://sdc-trip-car-management.herokuapp.com/car/find/hadwa";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, tripUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONObject obj = response;
                Car car = gson.fromJson(obj.toString(), Car.class);
                if(car.getCurrentTrip()!=null){
                    Trip trip = car.getCurrentTrip();

                    for(int i =0; i<trip.getDestinations().size(); i++){
                        TripDestination d= trip.getDestinations().get(i);
                        Markers.add(getGucPlaceByLatLng(d.getLocation()));
                    }

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
                                    GetRoutToMarker(pinLocations.get(Markers.get(0)));
                                    bottomSheet = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout);
                                    bottomSheet2 = (LinearLayout) getActivity().findViewById(R.id.BottomSheet_layout2);

                                    TextView dest1 = (TextView) getActivity().findViewById(R.id.dest1);
                                    TextView dest2 = (TextView) getActivity().findViewById(R.id.dest2);
                                    TextView dest3 = (TextView) getActivity().findViewById(R.id.dest3);
                                    TextView dest4 = (TextView) getActivity().findViewById(R.id.dest4);

                                    ImageView destIcon1 = (ImageView) getActivity().findViewById(R.id.dest_icon1);
                                    ImageView destIcon2 = (ImageView) getActivity().findViewById(R.id.dest_icon2);
                                    ImageView destIcon3 = (ImageView) getActivity().findViewById(R.id.dest_icon3);

                                    dest1.setVisibility(View.GONE);
                                    dest2.setVisibility(View.GONE);
                                    dest3.setVisibility(View.GONE);
                                    dest4.setVisibility(View.GONE);
                                    destIcon1.setVisibility(View.GONE);
                                    destIcon2.setVisibility(View.GONE);
                                    destIcon3.setVisibility(View.GONE);

                                    if (Markers.size() == 1) {
                                        dest1.setText(Markers.get(0));
                                        dest1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 2) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 3) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest3.setText(Markers.get(2));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        dest3.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                        destIcon2.setVisibility(View.VISIBLE);
                                    }
                                    if (Markers.size() == 4) {
                                        dest1.setText(Markers.get(0));
                                        dest2.setText(Markers.get(1));
                                        dest3.setText(Markers.get(2));
                                        dest4.setText(Markers.get(3));
                                        dest1.setVisibility(View.VISIBLE);
                                        dest2.setVisibility(View.VISIBLE);
                                        dest3.setVisibility(View.VISIBLE);
                                        dest4.setVisibility(View.VISIBLE);
                                        destIcon1.setVisibility(View.VISIBLE);
                                        destIcon2.setVisibility(View.VISIBLE);
                                        destIcon3.setVisibility(View.VISIBLE);
                                    }


                                    bottomSheet.setVisibility(View.GONE);
                                    bottomSheet2.setVisibility(View.VISIBLE);


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




    public void createTrip(){
        java.lang.String createTripUrl = "https://sdc-trip-car-management.herokuapp.com/car/create/trip";
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
        Log.d("trip-server-car", requestTrip.getCarID());
        java.lang.String str = gson.toJson(Trip.toTrip(requestTrip));
        JSONObject trip = new JSONObject();

        try {
            trip = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("trip-server", trip.toString());
        JsonObjectRequest tripRequest = new JsonObjectRequest(Method.POST, createTripUrl, trip, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                createdTrip = gson.fromJson(response.toString(),Trip.class);
                Intent visualizationIntent = new Intent(getActivity(), VisualizationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("destination", pinLocations.get(Markers.get(0)));
                visualizationIntent.putExtra("bundle", bundle);
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
        Bundle bundle = new Bundle();
        bundle.putParcelable("destination", pinLocations.get(Markers.get(0)));
        visualizationIntent.putExtra("bundle", bundle);
        //visualizationIntent.putExtra("destination", pinLocations.get(Markers.get(0)));
        startActivity(visualizationIntent);
        getActivity().getSupportFragmentManager().beginTransaction().remove(MapsFragment.this);
    }
    public void onCancel(){
        Markers.clear();
        appState = "initialState";
        removePolylines();
        bottomSheet.setVisibility(View.VISIBLE);
        bottomSheet2.setVisibility(View.GONE);

    }


    public void updateFCMServer(){
        Car car = new Car();
        car.setcarID("hadwa");
        car.setTabletFcmToken(MyFirebaseInstanceIDService.getToken());

        java.lang.String newTokenUrl = "https://sdc-trip-car-management.herokuapp.com/car/admin/token/tablet";
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


    }



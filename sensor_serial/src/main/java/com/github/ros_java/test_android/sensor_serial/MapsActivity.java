package com.github.ros_java.test_android.sensor_serial;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MapsActivity extends AppCompatActivity{


    private int backpress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //this.setTitle("Please choose your drop-off point");
        this.setTitle(Html.fromHtml("<font color='#000000'>Please choose your drop-off point</font>"));
        MapsFragment MapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_2, MapsFragment, "").commit();
    }


    public void onBackPressed(){
//        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();
        if(MapsFragment.appState == "initialState"){
            Toast.makeText(getApplicationContext(), " Initial state ", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
        if(MapsFragment.appState == "routeReady"){
            //Toast.makeText(getApplicationContext(), " Route ready", Toast.LENGTH_SHORT).show();
            MapsFragment.appState = "initialState";
            MapsFragment.removePolylines();
            MapsFragment.bottomSheet.setVisibility(View.VISIBLE);
            MapsFragment.bottomSheet2.setVisibility(View.GONE);
        }

    }


}

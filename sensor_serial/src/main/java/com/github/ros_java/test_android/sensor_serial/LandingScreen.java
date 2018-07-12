package com.github.ros_java.test_android.sensor_serial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class LandingScreen extends AppCompatActivity {

    ImageButton lidarView;
    ImageButton cameraView;
    ImageButton trip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);
        lidarView = findViewById(R.id.lidarView);
        cameraView = findViewById(R.id.cameraView);
        trip = findViewById(R.id.tripButton);

        trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingScreen.this, AppStart.class);
                startActivity(intent);
            }
        });
        lidarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingScreen.this, CamLidarVisualization.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingScreen.this, CamLidarVisualization.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}

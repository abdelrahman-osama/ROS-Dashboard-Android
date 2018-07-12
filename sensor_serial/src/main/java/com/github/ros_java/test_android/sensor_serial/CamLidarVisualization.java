package com.github.ros_java.test_android.sensor_serial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.ros.android.AppCompatRosActivity;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.view.RosImageView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import sensor_msgs.CompressedImage;

public class CamLidarVisualization extends AppCompatRosActivity {
    int mode;
    RosImageView<CompressedImage> streamView;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualization_cam_lidar);
        mode = (int) getIntent().getSerializableExtra("mode");

        Log.d("mode", String.valueOf(mode));
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        streamView =findViewById(R.id.vView);
        if(mode == 1)
            streamView.setTopicName("axis_camera/image_raw/compressed");
        if(mode == 2)
            streamView.setTopicName("camleft/raw/compressed");

        streamView.setMessageType(CompressedImage._TYPE);
        streamView.setMessageToBitmapCallable(new BitmapFromCompressedImage());
    }

    @Override
    protected void init(NodeMainExecutor nodeMainExecutor) {
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(getRosHostname());
        nodeConfiguration.setMasterUri(getMasterUri());
        nodeConfiguration.setNodeName("node1");
        nodeMainExecutor.execute(streamView, nodeConfiguration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

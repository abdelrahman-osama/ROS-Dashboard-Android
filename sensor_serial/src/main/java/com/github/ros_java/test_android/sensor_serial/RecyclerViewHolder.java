package com.github.ros_java.test_android.sensor_serial;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hadwa on 4/22/2018.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements ListItemTouchHelper {

        public final TextView textView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.CardText);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

    }

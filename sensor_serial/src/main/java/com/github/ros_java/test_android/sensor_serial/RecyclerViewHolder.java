package com.github.ros_java.test_android.sensor_serial;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hadwa on 4/22/2018.
 */
// TODO replace card with itemview
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements ListItemTouchHelper {

        public final TextView textView;
        public CardView card;


    public RecyclerViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.CardText);
            card=itemView.findViewById(R.id.card);

        }

        @Override
        public void onItemSelected() {
            card.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            card.setBackgroundColor(0);
        }



    }

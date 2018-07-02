package com.github.ros_java.test_android.sensor_serial;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Collections;

import static com.github.ros_java.test_android.sensor_serial.MapsFragment.appState;
import static com.github.ros_java.test_android.sensor_serial.MapsFragment.tripToBe;

/**
 * Created by hadwa on 4/22/2018.
 */

class ItemDragHelper extends ItemTouchHelper.Callback {
    private final RecyclerListAdapter itemHelper;
    public static final float ALPHA_FULL = 1.0f;

    public ItemDragHelper(RecyclerListAdapter touchHelper) {
        this.itemHelper = touchHelper;

    }

    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, ItemTouchHelper.ACTION_STATE_IDLE); // Flag Left is not used
    }

    public boolean isLongPressDragEnabled() {
        Log.d("isLongPress", "?");
        if(!RecyclerListAdapter.dragable && MapsFragment.appState == "modify") {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
          Log.d("brownies", "onSwiped");

//        if(direction== ItemTouchHelper.UP){
//            itemHelper.Markers.remove(itemHelper.Markers.get(viewHolder.getAdapterPosition()));
//            itemHelper.notifyItemRemoved(viewHolder.getAdapterPosition());
//            MapsFragment.DestinationCount--;
//            Log.d("brownies", String.valueOf(itemHelper.Markers.size()));
//        }

    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        if(MapsFragment.appState == "modify"  ) {
//            if(!MapsFragment.tripToBe.getDestinations().get(viewHolder.getAdapterPosition()).isArrived()){
//                itemHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//            }
//
//        }else {
        if(appState.equals("modify")) {
            if (!tripToBe.getDestinations().get(target.getAdapterPosition()).isArrived())
                itemHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }else{
            itemHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
  //      }
        return true;
    }
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {



        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            Log.d("DragHelper", "#1");
            if (viewHolder instanceof ListItemTouchHelper) {
            // Let the view holder know that this item is being moved or dragged
                Log.d("DragHelper", "#2");

            ((ListItemTouchHelper) viewHolder).onItemSelected();
        }
            //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);

        }
        Log.d("DragHelper", "#3");


        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        viewHolder.itemView.setAlpha(ALPHA_FULL);
  //      Log.d("osama","hena wala eh");
//        Log.d("brakessss", itemHelper.Markers.get(0));
 //       Log.d("brakesss", itemHelper.Markers.get(1));
        if (viewHolder instanceof ListItemTouchHelper) {
            // Tell the view holder it's time to restore the idle state
            ListItemTouchHelper itemViewHolder = (ListItemTouchHelper) viewHolder;
            itemViewHolder.onItemClear();
        }
    }
}

package com.github.ros_java.test_android.sensor_serial;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Collections;

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
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


//
//        if(direction== ItemTouchHelper.UP){
//            itemHelper.Markers.remove(itemHelper.Markers.get(viewHolder.getAdapterPosition()));
//            itemHelper.notifyItemRemoved(viewHolder.getAdapterPosition());
//            MapsFragment.DestinationCount--;
//            Log.d("brownies", String.valueOf(itemHelper.Markers.size()));
//        }

    }

    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemHelper.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {



        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            Log.d("DragHelperr", "am I here?");
            if (viewHolder instanceof ListItemTouchHelper) {
            // Let the view holder know that this item is being moved or dragged

            ((ListItemTouchHelper) viewHolder).onItemSelected();
        }
            //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);

        }

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

/*package com.example.hadwa.myapplication;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;






public class BottomSheet extends BottomSheetDialogFragment
         {

    private BottomSheetBehavior mBehavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottom_sheet_layout, null);

        //view.findViewById(R.id.fakeShadow).setVisibility(View.GONE);
        //RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //recyclerView.setHasFixedSize(true);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       // ItemAdapter itemAdapter = new ItemAdapter(createItems(), this);
        //recyclerView.setAdapter(itemAdapter);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }




}*/

/*
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


public class BottomSheet extends BottomSheetBehavior {

    private BottomSheetBehavior BottomSheetBehavior;
    private View bottomsheetFragment ;
    private TextView Marker;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v =inflater.inflate(R.layout.bottom_sheet_layout,container,false);
         bottomsheetFragment=v.findViewById(R.id.bottomsheet);
         BottomSheetBehavior=BottomSheetBehavior.from(bottomsheetFragment);
         Marker=v.findViewById(R.id.WhichStop);
        return v;
     }

     public void expand(com.google.android.gms.maps.model.Marker marker){
         BottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
         Marker.setText(marker.getTitle());

     }


}
*/


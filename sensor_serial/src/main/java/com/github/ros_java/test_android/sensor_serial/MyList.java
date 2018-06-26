package com.github.ros_java.test_android.sensor_serial;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.util.ArrayList;

public class MyList {

    private ArrayList<Marker> list;
    ListListener ll;

    public MyList(ListListener ll) {
        list = new ArrayList<>();
        this.ll = ll;
        }

        public void add(Marker t) {
        list.add(t);
        ll.onListChange();
            //do other things you want to do when items are added
        }

        public void remove(int t){
            list.remove(t);
            ll.onListChange();
            //do other things you want to do when items are removed
        }

        public Marker get(int i){
        return list.get(i);
        }

        public int size(){
            return list.size();
        }
        public void clear(){
            list.clear();
        }
        public ArrayList<Marker>  getList(){
            return list;
        }




    }


package com.ust.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.ust.smartph.ChecklistHomeActivity;
import com.ust.smartph.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ust.utility.Utils;
import java.util.HashMap;

public class CollabFilter {
    /**
     * this function return a collaborative filtering result randomly basically
     * */
    Context context;

    OnReceiveListener onReceiveListener;

    public CollabFilter(Context context,OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
        this.context=context;
    }

    public void filtering(){
        SharedPreferences sp =context.getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String userID = Utils.MD5(sp.getString("email", null),Utils.ID_LEN);
        HashMap<String,String> data=new HashMap<>();
        data.put("user_id",userID);
        data.put("num_user","2");

        String url = "http://13.70.2.33/api/distance_matrix/2";
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            if(result.length()==0){
                                return;
                            }
                            String uid=result.getString((int)Math.floor((Math.random()*result.length())));
                            onReceiveListener.onReceive(uid);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Make sure you have uploaded schedule history!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }
}

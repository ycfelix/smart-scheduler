package com.ust.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonIOException;
import com.ust.smartph.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public final class Utils {
    private static final String TAG = "UTILS";
    public static final String EMAIL_PWD = "EMAIL_PWD";
    public static final int PASS_LEN = 8;
    public static final int ID_LEN = 5;
    private Utils() {}

    // Volley request with custom timeout time
    public static void connectServer(JSONObject jsonData, String api, final int timeOut,
                                     final int REQUEST, Context context, final VolleyCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a JSONObject response from the provided URL.
        JsonObjectRequest accPostRequest = new JsonObjectRequest(REQUEST, api, jsonData,
                (JSONObject response) -> {
                    try {
                        Log.d(TAG, "Volley success (custom)");
                        callback.onSuccess(response);
                    }
                    catch (Exception e) {
                        Log.d(TAG, "Exception occurred in volley! (custom)");
                        Log.d(TAG, "Exception = " + e.toString());
                        callback.onFailure();
                    }
                },
                (VolleyError error) -> {
                    Log.d(TAG, "Volley failed (custom)");
                    Log.d(TAG, "Volley error = " + error.toString());
                    callback.onFailure();
                }
        );

        accPostRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(accPostRequest);
    }

    // Volley request using default Volley timeout time
    public static void connectServer(JSONObject jsonData, String api,
                                     final int REQUEST, Context context, final VolleyCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a JSONObject response from the provided URL.
        JsonObjectRequest accPostRequest = new JsonObjectRequest(Request.Method.POST, api, jsonData,
                (JSONObject response) -> {
                    try {
                        Log.d(TAG, "Volley success");
                        callback.onSuccess(response);
                    }
                    catch (Exception e) {
                        Log.d(TAG, "Exception occurred in volley!");
                        Log.d(TAG, "Exception = " + e.toString());
                        callback.onFailure();
                    }
                },
                (VolleyError error) -> {
                    Log.d(TAG, "Volley failed");
                    Log.d(TAG, "Volley error = " + error.toString());
                    callback.onFailure();
                }) ;
        // Add the request to the RequestQueue.
        queue.add(accPostRequest);
    }

    public static String MD5(String sourceStr, int numDigits) {
        if (numDigits > 32) numDigits = 32;
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
//            Log.d(TAG, "MD5(" + sourceStr + ",32) = " + result);
//            Log.d(TAG, "MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24)); // use this for UserID
//            Log.d(TAG, "MD5(" + sourceStr + ",5) = " + buf.toString().substring(0, numDigits));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result.substring(0, numDigits);
    }

    public enum DistMatrix {
        MAP(0), TIMETABLE(1), CHECKLIST(2), CALENDAR(3);

        int typ;
        DistMatrix(int typ) {this.typ = typ;}
        public int getTyp() {return typ;}
    }
}

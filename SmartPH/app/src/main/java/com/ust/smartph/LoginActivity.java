package com.ust.smartph;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passEditText;
    private final String TAG = "LOGIN";
    private JSONObject resultJSON = new JSONObject();

    // T table for Pearson hashing from RFC 3074.
    private char T[] = {
        251, 175, 119, 215, 81, 14, 79, 191, 103, 49, 181, 143, 186, 157,  0,
        232, 31, 32, 55, 60, 152, 58, 17, 237, 174, 70, 160, 144, 220, 90, 57,
        223, 59,  3, 18, 140, 111, 166, 203, 196, 134, 243, 124, 95, 222, 179,
        197, 65, 180, 48, 36, 15, 107, 46, 233, 130, 165, 30, 123, 161, 209, 23,
        97, 16, 40, 91, 219, 61, 100, 10, 210, 109, 250, 127, 22, 138, 29, 108,
        244, 67, 207,  9, 178, 204, 74, 98, 126, 249, 167, 116, 34, 77, 193,
        200, 121,  5, 20, 113, 71, 35, 128, 13, 182, 94, 25, 226, 227, 199, 75,
        27, 41, 245, 230, 224, 43, 225, 177, 26, 155, 150, 212, 142, 218, 115,
        241, 73, 88, 105, 39, 114, 62, 255, 192, 201, 145, 214, 168, 158, 221,
        148, 154, 122, 12, 84, 82, 163, 44, 139, 228, 236, 205, 242, 217, 11,
        187, 146, 159, 64, 86, 239, 195, 42, 106, 198, 118, 112, 184, 172, 87,
        2, 173, 117, 176, 229, 247, 253, 137, 185, 99, 164, 102, 147, 45, 66,
        231, 52, 141, 211, 194, 206, 246, 238, 56, 110, 78, 248, 63, 240, 189,
        93, 92, 51, 53, 183, 19, 171, 72, 50, 33, 104, 101, 69, 8, 252, 83, 120,
        76, 135, 85, 54, 202, 125, 188, 213, 96, 235, 136, 208, 162, 129, 190,
        132, 156, 38, 47, 1, 7, 254, 24, 4, 216, 131, 89, 21, 28, 133, 37, 153,
        149, 80, 170, 68, 6, 169, 234, 151
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        // Address the email and password field
        emailEditText = findViewById(R.id.username);
        passEditText = findViewById(R.id.password);

    }
    public void enterAboutUs(View arg){
        startActivity(new Intent(LoginActivity.this, AboutUsActivity.class));
    }

    public void forgetPassword(View arg){
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_password);
        dialog.show();
    }

    public void checkLogin(View arg0) {

        final String email = emailEditText.getText().toString();
        final String pass = passEditText.getText().toString();
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailEditText.setError("Invalid Email");
        }

        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("Invalid password");
        }
        if (isValidEmail(email) && isValidPassword(pass)) {
            final char hashed_pwd = phash(pass);
            Log.d(TAG, "hashed_pwd@checkLogin: " + hashed_pwd);
//            isValid(email, pass, (JSONObject result) -> resultJSON = result);
            if (isValid(email, pass, (JSONObject result) -> resultJSON = result)) {
                Log.d(TAG, "finish isValid, success");
                if (true) {
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
                else {
                    // Indicate error
                }
            }
            else {
                Log.d(TAG, "finish isValid, failed");
                // create a pop-up saying login failed.
            }
        }
    }

    // validating email id
    private boolean isValidEmail(String email) {
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        return (pass != null && pass.length() >= 4);
    }

    // send email to server for validation
    private boolean isValid(String email, String pass, final VolleyCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String api ="http://13.70.2.33:5000/api/user/login";
        final JSONObject accInfo = new JSONObject();
        try {
            accInfo.put("email", email);
            accInfo.put("hashed_pwd", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Request a JSONObject response from the provided URL.
        JsonObjectRequest accPostRequest = new JsonObjectRequest(Request.Method.POST, api, accInfo,
            (JSONObject response) -> {
                try {
                    Log.d(TAG, "Volley success");
                    callback.onSuccess(response);
                }
                catch (Exception e) {

                }
            },
            (VolleyError error) -> {
                Log.d(TAG, "Volley failed");
            }
        );

        // Add the request to the RequestQueue.
        queue.add(accPostRequest);

        return true;
    }

    private char phash(String key) {
        char hash = (char)(key.length() % 256);
        for (int i = 0; i < key.length(); ++i) hash = T[(int)(hash) ^ key.charAt(i)];
        return hash;
    }
}

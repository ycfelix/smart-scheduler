package com.ust.smartph;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ust.signup.SignupDialog;
import com.ust.signup.SigupDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LOGIN";
    private String resultStr = null;
    private Integer error_code = null;

    // T table for Pearson hashing from RFC 3074.
    private char T[] = {
            251, 175, 119, 215, 81, 14, 79, 191, 103, 49, 181, 143, 186, 157, 0,
            232, 31, 32, 55, 60, 152, 58, 17, 237, 174, 70, 160, 144, 220, 90, 57,
            223, 59, 3, 18, 140, 111, 166, 203, 196, 134, 243, 124, 95, 222, 179,
            197, 65, 180, 48, 36, 15, 107, 46, 233, 130, 165, 30, 123, 161, 209, 23,
            97, 16, 40, 91, 219, 61, 100, 10, 210, 109, 250, 127, 22, 138, 29, 108,
            244, 67, 207, 9, 178, 204, 74, 98, 126, 249, 167, 116, 34, 77, 193,
            200, 121, 5, 20, 113, 71, 35, 128, 13, 182, 94, 25, 226, 227, 199, 75,
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

    @BindView(R.id.username)
    EditText emailEditText;

    @BindView(R.id.password)
    EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate");
        tryAutoLogin();
    }

    private void tryAutoLogin() {
        SharedPreferences pref = getSharedPreferences("EMAIL", Context.MODE_PRIVATE);
        String email = pref.getString("EMAIL", "");
        String pass = pref.getString("PASS", "");
        if(TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)){
            //cannot find userinfo, dont login
            return;
        }
        loginToServer(email,pass);
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
    }

    public void enterAboutUs(View arg) {
        startActivity(new Intent(LoginActivity.this, AboutUsActivity.class));
    }

    public void signupNow(View v) {
        SignupDialog dialog = new SignupDialog(this);
        dialog.setSigupDialogListener(new SigupDialogListener() {
            @Override
            public void onEditResult(String email, String password) {
                emailEditText.setText(email);
                passEditText.setText(password);
            }

            @Override
            public char onGetHashedPwd(String pass) {
//                Log.d(TAG, "pass = " + pass);
//                Log.d(TAG, "hashed_pwd in listener = " + phash(pass));
                return phash(pass);
            }

            @Override
            public void onGetCnxn(JSONObject jsonData, String api, final VolleyCallback callback) {
                connectServer(jsonData, api, callback);
            }

            @Override
            public boolean onCheckEmail(String email) {
                return isValidEmail(email);
            }

            @Override
            public boolean onCheckPass(String pass) {
                return isValidPassword(pass);
            }
        });
        dialog.show();
    }

    public void forgetPassword(View arg) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.forget_password);
        dialog.show();
    }

    private void loginToServer(String email, String pass) {
        final String hashed_pwd = Character.toString(phash(pass));
        Log.d(TAG, "hashed_password = " + hashed_pwd);
        final JSONObject accInfo = new JSONObject();
        try {
            accInfo.put("email", email);
            accInfo.put("hashed_pwd", hashed_pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String api = "http://13.70.2.33:5000/api/user/login";
        Log.d(TAG, "hashed_pwd@checkLogin: " + hashed_pwd);

        connectServer(accInfo, api,
                new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            resultStr = result.getString("result");
                            error_code = result.getInt("error_code");
                            Log.d(TAG, "resultStr = " + resultStr);
                            Log.d(TAG, "error_code = " + error_code);
                            if (error_code == -1 || error_code == 0) {
                                if (error_code == 0) {
                                    SharedPreferences emailSP = getSharedPreferences("EMAIL", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = emailSP.edit();
                                    editor.putString("EMAIL", email);
                                    editor.putString("PASS", pass);
                                    editor.apply();
                                }
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            } else {
                                // Indicate authentication error
                                Log.d(TAG, "Authentication failed...");
                                emailEditText.setError("Wrong email or password.");
                            }

                        } catch (JSONException e) {
                            Log.d(TAG, "JSON retrieve result failed!");
                        }
                    }

                    @Override
                    public void onFailure() {
                        Log.d(TAG, "Failed to check!");
                        emailEditText.setError("Login timeout!");
                    }
                });
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
            loginToServer(email,pass);
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
    private void connectServer(JSONObject jsonData, String api, final VolleyCallback callback) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a JSONObject response from the provided URL.
        JsonObjectRequest accPostRequest = new JsonObjectRequest(Request.Method.POST, api, jsonData,
                (JSONObject response) -> {
                    try {
                        Log.d(TAG, "Volley success");
                        callback.onSuccess(response);
                    } catch (Exception e) {
                        Log.d(TAG, "Exception occurred in volley!");
                        Log.d(TAG, "Exception = " + e.toString());
                        callback.onFailure();
                    }
                },
                (VolleyError error) -> {
                    Log.d(TAG, "Volley failed");
                    Log.d(TAG, "Volley error = " + error.toString());
                    callback.onFailure();
                }
        );

        // Add the request to the RequestQueue.
        queue.add(accPostRequest);
    }

    private char phash(String key) {
        char hash = (char) (key.length() % 256);
        for (int i = 0; i < key.length(); ++i) hash = T[(int) (hash) ^ key.charAt(i)];
        return hash;
    }
}

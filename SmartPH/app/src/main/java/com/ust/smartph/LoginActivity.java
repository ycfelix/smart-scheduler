package com.ust.smartph;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.ust.signup.SignupDialog;
import com.ust.signup.SigupDialogListener;
import com.ust.utility.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * LoginActivity
 */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LOGIN";
    private String resultStr = null;
    private Integer error_code = null;


    @BindView(R.id.username)
    EditText emailEditText;

    @BindView(R.id.password)
    EditText passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        ButterKnife.bind(this);
    }

    public void enterAboutUs(View arg){
        startActivity(new Intent(LoginActivity.this, AboutUsActivity.class));
    }

    public void signupNow(View v){
        SignupDialog dialog=new SignupDialog(this);
        dialog.setSigupDialogListener(new SigupDialogListener() {
            @Override
            public void onEditResult(String email, String password) {
                emailEditText.setText(email);
                passEditText.setText(password);
            }

            @Override
            public String onGetHashedPwd(String pass, int numDigits) {
                return Utils.MD5(pass, numDigits);
            }

            @Override
            public void onGetCnxn(JSONObject jsonData, String api, final VolleyCallback callback) {
                Utils.connectServer(jsonData, api, Request.Method.POST, LoginActivity.this, callback);
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
            final String hashed_pwd = Utils.MD5(pass, Utils.PASS_LEN);
            Log.d(TAG, "hashed_password = " + hashed_pwd);
//            Log.d(TAG, "hashed_password (phash) = " + Character.toString(phash(pass)));
            final JSONObject accInfo = new JSONObject();
            try {
                accInfo.put("email", email);
                accInfo.put("hashed_pwd", hashed_pwd);
//                accInfo.put("hashed_pwd", phash(pass));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "hashed_pwd@checkLogin: " + hashed_pwd);

            Utils.connectServer(accInfo, getString(R.string.login_api), Request.Method.POST, LoginActivity.this,
                    new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        resultStr = result.getString("result");
                        error_code = result.getInt("error_code");
                        Log.d(TAG, "resultStr = " + resultStr);
                        Log.d(TAG, "error_code = " + error_code);
                        if (error_code == -1) {
                            SharedPreferences emailPwdSP = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = emailPwdSP.edit();
                            editor.putString("email", email);
                            editor.putString("hashed_pwd", hashed_pwd);
                            editor.apply();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
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

}

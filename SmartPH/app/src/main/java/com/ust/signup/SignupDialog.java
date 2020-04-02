package com.ust.signup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ust.smartph.DashboardActivity;
import com.ust.smartph.LoginActivity;
import com.ust.smartph.R;
import com.ust.smartph.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupDialog extends Dialog {

    private Context context;

    private SigupDialogListener signupDialogListener;

    private String TAG = "SIGN_UP";

    @BindView(R.id.input_email)
    EditText email;

    @BindView(R.id.input_mobile)
    EditText mobile;

    @BindView(R.id.input_password)
    EditText password;

    @BindView(R.id.input_repassword)
    EditText confirmPassword;

    @BindView(R.id.signup_button)
    Button signup;

    @BindView(R.id.link_login)
    TextView login;

    public void setSigupDialogListener(SigupDialogListener signupDialogListener) {
        this.signupDialogListener = signupDialogListener;
    }

    public SignupDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.singup_home);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.signup_button})
    void signupClick(View v){
        // this.signupDialogListener.onEditResult(email.getText().toString(),password.getText().toString());
        String Email = email.getText().toString();
        String pass = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        Log.d(TAG, "password received = " + pass);
        Log.d(TAG, "Confirm password received = " + confirmPass);
        if (pass.equals(confirmPass) && pass.length() >= 4 &&
                this.signupDialogListener.onCheckEmail(Email) &&
                this.signupDialogListener.onCheckPass(pass)) {
            Log.d(TAG, "password before hashing = " + pass);
            String hashed_pwd = Character.toString(this.signupDialogListener.onGetHashedPwd(pass));
            Log.d(TAG, hashed_pwd);

            password.setText("");
            confirmPassword.setText("");

            final JSONObject regInfo = new JSONObject();
            try {
                regInfo.put("email", Email);
                regInfo.put("phoneNum", mobile.getText().toString());
                regInfo.put("hashed_pwd", hashed_pwd);
                regInfo.put("ID", (int)this.signupDialogListener.onGetHashedPwd(InstanceID.getInstance(context).getId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String api = "http://13.70.2.33:5000/api/user/register";

            this.signupDialogListener.onGetCnxn(regInfo, api, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String resultStr = result.getString("result");
                        int error_code = result.getInt("error_code");
                        String error_msg = result.getString("error_msg");
                        Log.d(TAG, "resultStr = " + resultStr);
                        Log.d(TAG, "error_code = " + error_code);
                        Log.d(TAG, "error_msg = " + error_msg);
                        if (error_code == -1) {
                            Toast.makeText(context, "Account registration successful", Toast.LENGTH_LONG).show();
                        }
                        else {
                            // Indicate authentication error
                            Toast.makeText(context, "Account registration failed with error code " + error_code
                                    + " and error message " + error_msg, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) { Log.d(TAG, "JSON retrieve result failed!"); }
                    Log.d(TAG, "Success");
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Account registration failed", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Failed");
                }
            });
            dismiss();
        }
        else {
            if (pass.length() < 4) {
                password.setError("Password cannot be shorter than 4.");
                password.setText("");
                confirmPassword.setText("");
            }
            if (!pass.equals(confirmPass)) {
                password.setError("Please check if both passwords are the same.");
                password.setText("");
                confirmPassword.setText("");
//                confirmPassword.setError("");
            }
        }
    }

    @OnClick({R.id.link_login})
    void backToLogin(View v) {
        dismiss();
    }

}

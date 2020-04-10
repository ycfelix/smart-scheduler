package com.ust.signup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.smartph.R;
import com.ust.smartph.VolleyCallback;
import com.ust.utility.Utils;

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
        String Email = email.getText().toString();
        String pass = password.getText().toString();
        String confirmPass = confirmPassword.getText().toString();
        Log.d(TAG, "password received = " + pass);
        Log.d(TAG, "Confirm password received = " + confirmPass);

        if (pass.equals(confirmPass) &&
            this.signupDialogListener.onCheckEmail(Email) &&
            this.signupDialogListener.onCheckPass(pass) &&
            mobile.getText().toString().length() > 0) {
            Log.d(TAG, "password before hashing = " + pass);
            String hashed_pwd = this.signupDialogListener.onGetHashedPwd(pass, Utils.PASS_LEN);
            Log.d(TAG, "hashed pwd = " + hashed_pwd);
            String id = this.signupDialogListener.onGetHashedPwd(Email, Utils.ID_LEN);
            Log.d(TAG, "id = " + id);

            password.setText("");
            confirmPassword.setText("");

            final JSONObject regInfo = new JSONObject();
            try {
                regInfo.put("email", Email);
                regInfo.put("phoneNum", mobile.getText().toString());
                regInfo.put("hashed_pwd", hashed_pwd);
                regInfo.put("ID", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String api = context.getString(R.string.register_api);

            this.signupDialogListener.onGetCnxn(regInfo, api, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result){
                    try {
                        String resultStr = result.getString("result");
                        int error_code = result.getInt("error_code");
                        String error_msg = result.getString("error_msg");
                        Log.d(TAG, "resultStr = " + resultStr);
                        Log.d(TAG, "error_code = " + error_code);
                        Log.d(TAG, "error_msg = " + error_msg);

                        if (error_code == -1) {
                            Toast.makeText(context, "Account registration successful." +
                                    "\nUse your newly created account to login!", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Success");
                            signupDialogListener.onEditResult(email.getText().toString(),password.getText().toString());
                        }
                        else {
                            // Indicate authentication error
//                            Toast.makeText(context, "Account registration failed with error code " + error_code
//                                    + " and error message " + error_msg, Toast.LENGTH_LONG).show();
                            Toast.makeText(context, "Account registration failed: " + error_msg, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.d(TAG, "JSON retrieve result failed!");
                        String report_error = "";
                        try {
                            report_error = result.getString("error");
                        } catch (JSONException JSON) {JSON.printStackTrace();}
                        Toast.makeText(context, "Please report bug in \"About Us\": " + report_error, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure() {
                    Toast.makeText(context, "Account registration failed. Please try again later", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Failed");
                }
            });
            dismiss();
        }
        else {
            if (!this.signupDialogListener.onCheckEmail(Email)) {
                email.setFocusableInTouchMode(true);
                email.requestFocus();
                email.setError("Invalid email.");
                email.setText("");
//                confirmPassword.setError("");
            }
            if (pass.length() < 4) {
//                password.requestFocus();
                password.setError("Password cannot be shorter than 4 characters.");
                confirmPassword.setError("Password cannot be shorter than 4 characters.");
                password.setText("");
                confirmPassword.setText("");
            }
            if (!pass.equals(confirmPass)) {
                confirmPassword.setError("Please check if both passwords are the same.");
                confirmPassword.setText("");
//                confirmPassword.setError("");
            }
            if (mobile.getText().toString().length() == 0) {
                mobile.setError("Please enter your mobile phone number");
            }
        }
    }

    @OnClick({R.id.link_login})
    void backToLogin(View v) {
        dismiss();
    }

}

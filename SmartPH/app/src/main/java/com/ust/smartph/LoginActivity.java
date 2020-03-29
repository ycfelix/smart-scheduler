package com.ust.smartph;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.ust.signup.SignupDialog;
import com.ust.signup.SigupDialogListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginActivity extends AppCompatActivity {

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
        if (!isValidEmail(email)) {
            //Set error message for email field
            emailEditText.setError("Invalid Email");
        }

        final String pass = passEditText.getText().toString();
        if (!isValidPassword(pass)) {
            //Set error message for password field
            passEditText.setError("Password cannot be empty");
        }

        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));


//        if(isValidEmail(email) && isValidPassword(pass))
//        {
//            // Validation Completed
//            startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
//        }
//        else {
//            startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
//        }

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
        if (pass != null && pass.length() >= 4) {
            return true;
        }
        return false;
    }
}

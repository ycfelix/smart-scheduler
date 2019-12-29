package com.ust.smartph;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText passEditText;

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
        Toast.makeText(this,"Coming Soon",Toast.LENGTH_LONG).show();
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

        if(email.isEmpty() && pass.isEmpty()){
            startActivity(new Intent(LoginActivity.this, ChecklistActivity.class));
        }
        if(email.equals("timetable")){
            startActivity(new Intent(LoginActivity.this,TimetableActivity.class));
        }
        else{
            startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
        }
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
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
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

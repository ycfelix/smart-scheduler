package com.ust.signup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ust.smartph.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupDialog extends Dialog {

    private Context context;

    private SigupDialogListener sigupDialogListener;

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

    public void setSigupDialogListener(SigupDialogListener sigupDialogListener) {
        this.sigupDialogListener = sigupDialogListener;
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

    @OnClick({R.id.signup_button,R.id.link_login})
    void signupClick(View v){
        this.sigupDialogListener.onEditResult(email.getText().toString(),password.getText().toString());
        dismiss();
    }


}

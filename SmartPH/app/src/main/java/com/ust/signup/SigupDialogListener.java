package com.ust.signup;

import org.json.JSONObject;
import com.ust.smartph.VolleyCallback;

public interface SigupDialogListener {
    void onEditResult(String email,String password);
    String onGetHashedPwd(String pass, int numDigits);
    void onGetCnxn(JSONObject jsonData, String api, final VolleyCallback callback);
    boolean onCheckEmail (String email);
    boolean onCheckPass (String email);
}

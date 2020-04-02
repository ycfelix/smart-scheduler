package com.ust.signup;

import org.json.JSONObject;
import com.ust.smartph.VolleyCallback;

public interface SigupDialogListener {
    void onEditResult(String email,String password);
    char onGetHashedPwd(String pass);
    void onGetCnxn(JSONObject jsonData, String api, final VolleyCallback callback);
    boolean onCheckEmail (String email);
    boolean onCheckPass (String email);
}

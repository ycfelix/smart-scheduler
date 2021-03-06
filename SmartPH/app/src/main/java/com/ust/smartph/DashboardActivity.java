package com.ust.smartph;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.ust.friend.DeleteItemListener;
import com.ust.friend.Friend;
import com.ust.friend.FriendAdapter;
import com.ust.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.friend_menu)
    ImageButton menu;

    @BindView(R.id.friend_add)
    ImageView addFriend;

    @BindView(R.id.friend_list)
    RecyclerView friendList;

    @BindView(R.id.user_name)
    TextView name;

    @BindView(R.id.last_login)
    TextView lastLogin;

    @BindView(R.id.friend_drawer)
    DrawerLayout drawer;

    @BindView(R.id.nav_friend)
    NavigationView friendNavigation;

    @BindView(R.id.signout)
    Button signOut;

    ArrayList<Friend> friends=new ArrayList<>();

    private FriendAdapter adapter;

    private String MD5(String s){
        return Utils.MD5(s, Utils.ID_LEN);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_friend);
        ButterKnife.bind(this);
        SharedPreferences pref = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
        String email = pref.getString("email", "");
        System.out.println("UID: "+MD5(email));
        System.out.println("email: "+email);
        name.setText("UID: "+MD5(email));
        friendList.setLayoutManager(new LinearLayoutManager(this));
        adapter=new FriendAdapter(this,friends);
        friendList.setAdapter(adapter);
        adapter.setDeleteItemListener(new DeleteItemListener() {
            @Override
            public void onDeleteItem() {
                deleteFriendFromDB(email,null,null,false);
            }
        });
        lastLogin.setText(Calendar.getInstance().getTime().toString());
        getFriendFromServer(email);

        // test distance matrix API
        final JSONObject distMatrix = new JSONObject();
        try {
            distMatrix.put("user_id", "test@testEmail.com");
            distMatrix.put("num_user", "1");
//            System.out.println(distMatrix.getString("user_id"));
//            System.out.println(distMatrix.getString("num_user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        String data = "{"+
//                "\"user_id\"" + "\"" + "test@testEmail.com" + "\","+
//                "\"num_user\"" + "\"" + "3" + "\""+
//                "}";
//        submit(data);

        Utils.connectServer(distMatrix,
                "http://13.70.2.33/api/distance_matrix/" + Utils.DistMatrix.CALENDAR.getTyp(),
                Request.Method.POST, getApplicationContext(), new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        System.out.println("It works!");
                        System.out.println(result);
                    }

                    @Override
                    public void onFailure() {
                        System.out.println("It doesn't work...");
                    }
                });
    }

    private void getFriendFromServer(String myEmail){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand="Select * from dbo.user_friend where user_email= '"+myEmail+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        System.out.println("getting friends");
        String url = this.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            System.out.println(response);
                            if(result.length()==0 || result.getJSONObject(0).getString("friend_list").equals("null")){
                                return;
                            }
                            addToFriendlist(result);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("cannot get frds");
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }

    @OnClick(R.id.signout)
    public void signOut(View v){
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure to sign-out?");
        dialog.setPositiveButton("Confirm", (dialog12, which) -> {
            SharedPreferences pref=getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=pref.edit();
            editor.putString("email",null);
            editor.putString("hashed_pwd",null);
            editor.commit();
            startActivity(new Intent(DashboardActivity.this,LoginActivity.class));
            dialog12.dismiss();
            DashboardActivity.this.finish();
        });
        dialog.setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

    void addToFriendlist(JSONArray arr) throws JSONException {
        ArrayList<Friend> dataModels=new ArrayList<>();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        for(int i=0;i<arr.length();i++){
            JSONObject row=arr.getJSONObject(i);
            String[] csv=row.getString("friend_list").split(",");
            Arrays.stream(csv).forEach(e-> {
                String name=preferences.getString(e,"");
                if(!TextUtils.isEmpty(name)){
                    friends.add(new Friend(name,MD5(e),false,e));
                }
                else{
                    friends.add(new Friend("friend_"+MD5(e),MD5(e),false,e));
                }
            });

        }
        friends.forEach(e-> System.out.println(e.getName()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.friend_add)
    void addFriend(View v){
        final EditText editText = new EditText(this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(this);
        inputDialog.setTitle("Enter your friends id...").setView(editText);
        inputDialog.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(TextUtils.isEmpty(editText.getText())){
                            Toast.makeText(DashboardActivity.this,"wrong input!",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            SharedPreferences pref = getSharedPreferences(Utils.EMAIL_PWD, Context.MODE_PRIVATE);
                            String email = pref.getString("email", "");
                            findFriendFromDB(email,editText.getText().toString());
                            dialog.dismiss();
                        }
                        //search on server
                    }
                });
        inputDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        inputDialog.show();
    }

    private void findFriendFromDB(String myEmail,String fdID){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand="select * from dbo.Accounts where UserID= '"+fdID+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = this.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response);
                            JSONArray arr=response.getJSONArray("result");
                            if(arr.length()>0){
                                String fdEmail=arr.getJSONObject(0).getString("Email");
                                if(friends.stream().anyMatch(e-> e.getEmail().equals(fdEmail))){
                                    Toast.makeText(DashboardActivity.this,"friend already exist!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                deleteFriendFromDB(myEmail,fdEmail,fdID,true);
                            }
                            else{
                                Toast.makeText(DashboardActivity.this, "Friend doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);

    }

    private void deleteFriendFromDB(String myEmail,String fdEmail,String fdID, boolean add){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand="delete from dbo.user_friend where user_email= '"+myEmail+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = this.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(add){
                            System.out.println("adding frid!"+fdID+fdEmail);
                            Friend fd=new Friend("new friend",fdID,false,fdEmail);
                            friends.add(fd);
                            adapter.notifyItemInserted(friends.size()-1);
                        }
                        String[] arr=friends.stream().map(Friend::getEmail).toArray(String[]::new);
                        addFriendToDB(myEmail,arr);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }


    private void addFriendToDB(String myEmail, String[] csv){
        HashMap<String,String> data=new HashMap<>();
        String fds=csv.length==0?null:String.join(",",csv);
        String sqlCommand=String.format(Locale.US,
                "insert into dbo.user_friend(user_email,friend_list) values('%s','%s')", myEmail,fds);
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = this.getString(R.string.sql_api);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("inserted frd!");
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
        queue.add(request);
    }


    @OnClick(R.id.friend_menu)
    void setMenuClick(View arg) {
        drawer.openDrawer(GravityCompat.START);
        adapter.notifyDataSetChanged();
    }


    public void startAboutUs(View arg){
        startActivity(new Intent(this, AboutUsActivity.class));
    }
    public void startCalendar(View arg){
        startActivity(new Intent(this, CalendarActivity.class));
    }
    public void startTimetable(View arg){
        startActivity(new Intent(this, TimetableHomeActivity.class));
    }
    public void startChecklist(View arg){
        startActivity(new Intent(this, ChecklistHomeActivity.class));
    }
    public void startAppUsage(View arg){
        startActivity(new Intent(this, AppUsageHomeActivity.class));
    }

    public void toBeContinue(View arg){
        Toast.makeText(this.getApplicationContext(),"coming soon",Toast.LENGTH_SHORT).show();
    }
    public void startFocusMode(View arg){
        startActivity(new Intent(this, CustomFocusModeActivity.class));
    }

    public void startActionDetection(View arg){
        startActivity(new Intent(this, UserActionActivity.class));
    }

    @Override
    public void onBackPressed() {
//        Intent startMain = new Intent(Intent.ACTION_MAIN);
//        startMain.addCategory(Intent.CATEGORY_HOME);
//        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(startMain);
        finish();
    }
}

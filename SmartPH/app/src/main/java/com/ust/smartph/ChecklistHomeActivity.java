package com.ust.smartph;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ust.customchecklist.ChecklistAdapter;
import com.ust.customchecklist.DataModel;
import com.ust.customchecklist.EditDialog;
import com.ust.customchecklist.EditDialogListener;
import com.ust.customchecklist.ItemClickListener;
import com.ust.customchecklist.RequestType;
import com.ust.timetable.HashGenerator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ChecklistHomeActivity extends AppCompatActivity {

    @BindView(R.id.checklist_list)
    RecyclerView checklist;

    @BindView(R.id.checklist_import_fab)
    FloatingActionButton importBtn;

    @BindView(R.id.checklist_export_fab)
    FloatingActionButton exportBtn;

    @BindView(R.id.checklist_debug_fab)
    FloatingActionButton debugBtn;

    @BindView(R.id.checklist_add_fab)
    FloatingActionButton addBtn;

    @BindView(R.id.checklist_fab_menu)
    FloatingActionMenu menu;

    ArrayList<DataModel> data;

    private ChecklistAdapter adapter;

    private static final String PREF_NAME="checklist";

    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_home);
        unbinder= ButterKnife.bind(this);
        loadDataFromPreference();
        checklist.setLayoutManager(new LinearLayoutManager(this));
        adapter=new ChecklistAdapter(this, data);
        checklist.setAdapter(adapter);
    }

    @OnClick(R.id.checklist_add_fab)
    void addChecklist(View v){
        menu.close(true);
        EditDialog dialog = new EditDialog(this, RequestType.ADD);
        dialog.setEditDialogListener(new EditDialogListener() {
            @Override
            public void onEditResult(@NotNull DataModel data, RequestType type) {
                ChecklistHomeActivity.this.data.add(data);
                adapter.notifyItemInserted(ChecklistHomeActivity.this.data.size()-1);
                saveDataToPreference();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.checklist_debug_fab)
    void debug(View v){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=pref.edit();
        Map<String,?> prefs =pref.getAll();
        Set<String> keys =new TreeSet<>(prefs.keySet());
        keys.forEach(e->{
            if(e.equals(PREF_NAME)){
                System.out.println("key is "+e);
                editor.remove(e);
            }
        });
        editor.commit();
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.checklist_import_fab)
    void importChecklist(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_import,null);
        builder.setTitle("Input the generated number");
        builder.setView(dialogView);
        builder.setPositiveButton("enter",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText tokenEt = dialogView.findViewById(R.id.import_token);
                        String token=tokenEt.getText().toString();
                        if(!TextUtils.isEmpty(token)){
                            getChecklistFromServer(token);
                            menu.close(true);
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"wrong input!",Toast.LENGTH_SHORT);
                        }
                    }
                });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    void getChecklistFromServer(String token){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand="Select * from dbo.user_checklist where token= '"+token+"'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);

        String url = this.getString(R.string.server_ip);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray result= response.getJSONArray("result");
                            System.out.println(response);
                            if(result.length()==0){
                                return;
                            }
                            addToChecklist(result);
                            adapter.notifyItemInserted(ChecklistHomeActivity.this.data.size()-1);
                            saveDataToPreference();

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

    void addToChecklist(JSONArray arr) throws JSONException {
        ArrayList<DataModel> dataModels=new ArrayList<>();
        for(int i=0;i<arr.length();i++){
            JSONObject row=arr.getJSONObject(i);
            DataModel model=new DataModel();
            model.setChecked(row.getBoolean("checked"));
            model.setDetail(row.getString("detail"));
            model.setIcon(row.getInt("icon"));
            model.setTitle(row.getString("title"));
            dataModels.add(model);
        }
        int size=data.size();
        data.addAll(dataModels);
        adapter.notifyItemRangeInserted(size,data.size()-1);
    }


    private String getToken(String timetableName){
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return HashGenerator.toHashCode(android_id)+HashGenerator.toHashCode(timetableName);
    }

    void updateDB(){
        HashMap<String,String> data=new HashMap<>();
        String sqlCommand= "delete from dbo.user_checklist where token = '%s'";
        data.put("db_name","Smart Scheduler");
        data.put("sql_cmd",sqlCommand);
        String url = this.getString(R.string.server_ip);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        sendChecklistToServer();
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


    @OnClick(R.id.checklist_export_fab)
    void exportChecklist(View v){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_export, null);
        builder.setTitle("Your generated share code");
        updateDB();
        TextView tokenTv = dialogView.findViewById(R.id.export_token);
        String token = getToken(PREF_NAME);
        tokenTv.setText(token);
        builder.setView(dialogView);
        builder.setPositiveButton("Share",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, token);
                        sendIntent.setType("text/plain");
                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                        startActivity(shareIntent);
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    private void sendChecklistToServer(){
        String url = this.getString(R.string.server_ip);
        List<String> commands=getSQLCommands();
        for(int i=0;i<commands.size();i++){
            HashMap<String,String> data=new HashMap<>();
            data.put("db_name","Smart Scheduler");
            data.put("sql_cmd",commands.get(i));

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }
            );
            queue.add(request);
        }
    }

    @NotNull
    private List<String> getSQLCommands(){
       String token=getToken(PREF_NAME);
       ArrayList<String> sql=new ArrayList<>();
       for(DataModel model:data){
           boolean checked=model.isChecked();
           String title=model.getTitle();
           String detail=model.getDetail();
           int icon=model.getIcon();
           sql.add(String.format(Locale.US,
                   "insert into dbo.user_checklist(checked,title,detail,icon,"+
                           "token) values(%d,'%s', '%s',%d,'%s')",checked?1:0,title,detail,icon,token));
       }
       return sql;
    }



    void saveDataToPreference(){
        Gson gson=new Gson();
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString(PREF_NAME,gson.toJson(data));
        editor.commit();
    }

    void loadDataFromPreference(){
        SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(this);
        String raw=pref.getString(PREF_NAME,"");
        if(!TextUtils.isEmpty(raw)){
            Gson gson=new Gson();
            this.data=gson.fromJson(raw,new TypeToken<ArrayList<DataModel>>(){}.getType());
        }
        else{
            this.data=new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

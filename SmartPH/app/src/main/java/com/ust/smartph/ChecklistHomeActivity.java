package com.ust.smartph;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        adapter=new ChecklistAdapter(this, data, new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                EditDialog dialog = new EditDialog(ChecklistHomeActivity.this, RequestType.EDIT,data.get(position));
                dialog.setEditDialogListener(new EditDialogListener() {
                    @Override
                    public void onEditResult(@NotNull DataModel data, RequestType type) {
                        if(type==RequestType.DELETE){
                            ChecklistHomeActivity.this.data.remove(position);
                            adapter.notifyItemRemoved(position);
                        }
                        else{
                            ChecklistHomeActivity.this.data.set(position,data);
                            adapter.notifyItemChanged(position);
                        }
                        saveDataToPreference();
                    }
                });
                dialog.show();
            }
        });
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
                adapter.notifyDataSetChanged();
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

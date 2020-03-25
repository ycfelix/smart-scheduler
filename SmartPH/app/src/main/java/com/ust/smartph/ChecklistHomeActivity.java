package com.ust.smartph;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ust.customchecklist.ChecklistAdapter;
import com.ust.customchecklist.DataModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChecklistHomeActivity extends AppCompatActivity {

    @BindView(R.id.checklist_toolbar)
    Toolbar toolbar;

    @BindView(R.id.checklist_list)
    ListView checklist;

    ArrayList<DataModel> data;

    private ChecklistAdapter adapter;

    Unbinder unbinder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist_home);
        unbinder= ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getTestData();
        adapter=new ChecklistAdapter(this,data);
        checklist.setAdapter(adapter);
        checklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ChecklistHomeActivity.this,
                        data.get(position).toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void getTestData(){
        data=new ArrayList<>();
        DataModel s=new DataModel("test title","test detail",R.drawable.ic_fitness_center_white_24dp,false);
        data.add(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}

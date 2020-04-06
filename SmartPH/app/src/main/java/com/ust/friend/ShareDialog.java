package com.ust.friend;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.smartph.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareDialog extends Dialog {

    @BindView(R.id.name_edit)
    TextView name;

    @BindView(R.id.confirm)
    Button confirm;

    @BindView(R.id.cancel)
    Button cancel;

    EditNameListener editNameListener;

    @BindView(R.id.shared_item)
    RecyclerView shareItems;

    ShareItemAdapter adapter;

    ArrayList<Share> shares;

    String myName;

    public void setEditNameListener(EditNameListener editNameListener) {
        this.editNameListener = editNameListener;
    }

    public ShareDialog(@NonNull Context context , ArrayList<Share> shares,String fdName) {
        super(context);
        this.shares=shares;
        myName=fdName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_edit);
        ButterKnife.bind(this);
        shareItems.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ShareItemAdapter(getContext(),shares);
        name.setText(myName);
        shareItems.setAdapter(adapter);
    }

    @OnClick({R.id.confirm,R.id.cancel})
    void done(View v){
        if(v.getId()==R.id.confirm){
            if(TextUtils.isEmpty(name.getText().toString())){
                Toast.makeText(getContext(), "cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            editNameListener.onEditResult(name.getText().toString());
        }
        dismiss();
    }


}

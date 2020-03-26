package com.ust.customchecklist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ust.smartph.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditDialog extends Dialog {

    private EditDialogListener editDialogListener;

    @BindView(R.id.checklist_title)
    TextView title;

    @BindView(R.id.checklist_detail)
    TextView detail;

    @BindView(R.id.icon_grid)
    GridLayout iconSection;

    @BindView(R.id.delete)
    Button delete;

    @BindView(R.id.edit)
    Button edit;

    private int icon;

    private Context context;

    private RequestType request;

    private DataModel checklist;

    public EditDialog(@NonNull Context context,RequestType type) {
        super(context);
        this.request=type;
        this.context=context;
    }

    public EditDialog(@NonNull Context context,RequestType type,DataModel checklist) {
        super(context);
        this.request=type;
        this.checklist=checklist;
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.checklist_edit);
        ButterKnife.bind(this);
        if(request==RequestType.EDIT){
            delete.setText("delete");
        }
        if(checklist!=null){
            loadChecklistData();
        }
        else{
            checklist=new DataModel();
        }
        initIconSection();
    }

    private void initIconSection(){
        int size=iconSection.getChildCount();
        for(int i=0;i<size;i++){
            ImageView icon= (ImageView) iconSection.getChildAt(i);
            if(i==0){
                this.icon=icon.getId();
                icon.setBackgroundResource(R.drawable.color_splotch_selected);
            }
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repaintIcons();
                    ((ImageView)v).setBackgroundResource(R.drawable.color_splotch_selected);
                    EditDialog.this.icon=icon.getId();
                }
            });
        }
    }

    private void repaintIcons(){
        int size=iconSection.getChildCount();
        for(int i=0;i<size;i++) {
            ImageView icon = (ImageView) iconSection.getChildAt(i);
            icon.setBackgroundResource(R.drawable.color_splotch_selector);
        }
    }

    @OnClick({R.id.delete,R.id.edit})
    void finishModifyChecklist(View v){
        checklist.setTitle(title.getText().toString());
        checklist.setDetail(detail.getText().toString());
        checklist.setIcon(this.icon);
        if(checklist.getTitle().isEmpty()){
            if(v.getId()==R.id.delete){
                dismiss();
                return;
            }
            Toast.makeText(context, "title cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        //special case to delete item, set the mode to delete and put in onResult
        if(v.getId()==R.id.delete){
            this.request=RequestType.DELETE;
        }
        this.editDialogListener.onEditResult(checklist,this.request);
        dismiss();
    }

    public void setEditDialogListener(EditDialogListener onEditResult) {
        this.editDialogListener = onEditResult;
    }

    private void loadChecklistData(){
        title.setText(checklist.getTitle());
        detail.setText(checklist.getDetail());
        icon=checklist.getIcon();
    }
}

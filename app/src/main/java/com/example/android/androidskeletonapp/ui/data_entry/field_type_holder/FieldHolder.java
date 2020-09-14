package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

class FieldHolder extends RecyclerView.ViewHolder {

    final FormAdapter.OnValueSaved valueSavedListener;
    TextView label;
    TextView label1;
    TextView label2;
    TextView label3;
    ImageButton button1;

    FieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.label1 = itemView.findViewById(R.id.label1);
        this.label2 = itemView.findViewById(R.id.label2);
        this.label3 = itemView.findViewById(R.id.label3);
        this.button1 = itemView.findViewById(R.id.helpButton);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(FormField fieldItem) {
        label.setText(fieldItem.getFormLabel());
        label.setTextSize(20);
        label.setTextColor(Color.BLACK);
        label.setTypeface(label.getTypeface(), Typeface.BOLD);

        if(fieldItem.getFormDescription() == null || fieldItem.getFormDescription().isEmpty()) {
            button1.setVisibility(View.GONE);
        } else {
            button1.setVisibility(View.VISIBLE);
            button1.setOnClickListener(view -> {
                if(label1.getVisibility() == View.GONE){
                    label1.setVisibility(View.VISIBLE);
                    label1.setText(fieldItem.getFormDescription());
                    label1.setTextSize(17);
                } else {
                    label1.setVisibility(View.GONE);
                }
            });
        }

        if(fieldItem.getFormWarning() != null && fieldItem.getFormWarning() != ""){
            label2.setVisibility(View.VISIBLE);
            label2.setText(fieldItem.getFormWarning());
            label2.setTextSize(18);
            label2.setTextColor(Color.rgb(128,0,0));
        }
        else {
            label2.setVisibility(View.GONE);
            label2.setText("");
        }

        if(fieldItem.getFormError() != null && fieldItem.getFormError() != ""){
            label3.setVisibility(View.VISIBLE);
            label3.setText(fieldItem.getFormError());
            label3.setTextSize(18);
            label3.setTextColor(Color.RED);
        }
        else {
            label3.setVisibility(View.GONE);
            label3.setText("");
        }
    }
}

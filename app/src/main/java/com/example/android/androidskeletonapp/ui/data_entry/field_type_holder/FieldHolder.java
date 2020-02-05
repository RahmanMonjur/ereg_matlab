package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

class FieldHolder extends RecyclerView.ViewHolder {

    final FormAdapter.OnValueSaved valueSavedListener;
    TextView label;
    TextView label1;

    FieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.label1 = itemView.findViewById(R.id.label1);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(FormField fieldItem) {
        label.setText(fieldItem.getFormLabel());
        label.setTextSize(20);
        label.setTextColor(Color.BLACK);
        label.setTypeface(label.getTypeface(), Typeface.BOLD);

        if(fieldItem.getFormHint() != null && fieldItem.getFormHint() != ""){
            label1.setVisibility(View.VISIBLE);
            label1.setText(fieldItem.getFormHint());
            label1.setTextSize(18);
            label1.setTextColor(Color.RED);
        }
        else {
            label1.setVisibility(View.GONE);
            label1.setText("");
        }
    }
}

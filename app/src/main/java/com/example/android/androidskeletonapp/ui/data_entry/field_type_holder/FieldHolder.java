package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

class FieldHolder extends RecyclerView.ViewHolder {

    final FormAdapter.OnValueSaved valueSavedListener;
    TextView label;

    FieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(FormField fieldItem) {
        label.setText(fieldItem.getFormLabel());
        label.setTextSize(16);
        label.setTextColor(Color.BLACK);
    }
}

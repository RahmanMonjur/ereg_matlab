package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.common.ValueType;

public class CheckboxFieldHolder extends FieldHolder {

    private final CheckBox checkBox;

    CheckboxFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.checkBox = itemView.findViewById(R.id.checkBox);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        if (fieldItem.getValue() != null && fieldItem.getValue().equals("true")) {
            checkBox.setChecked(true);
        }  else {
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener((view, isChecked) -> {
            String fieldUid = fieldItem.getUid();
            valueSavedListener.onValueSaved(fieldUid, isChecked ? "true" : "");
        });




    }
}


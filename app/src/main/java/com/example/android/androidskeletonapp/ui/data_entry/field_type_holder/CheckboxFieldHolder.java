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
    private boolean controlsEnable;

    CheckboxFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener, boolean controlsEnable) {
        super(itemView, valueSavedListener);
        this.checkBox = itemView.findViewById(R.id.checkBox);
        this.controlsEnable = controlsEnable;
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        checkBox.setOnCheckedChangeListener(null);

        if (fieldItem.getValue() != null && fieldItem.getValue().equals("true")) {
            checkBox.setChecked(true);
        }  else {
            checkBox.setChecked(false);
        }

        setCheckBox(fieldItem.getUid());

        checkBox.setEnabled(controlsEnable);
    }

    void setCheckBox(String field){
        checkBox.setOnCheckedChangeListener((view, isChecked) -> {
            valueSavedListener.onValueSaved(field, isChecked ? "true" : "");
        });
    }
}


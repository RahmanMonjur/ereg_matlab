package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;

import org.hisp.dhis.android.core.common.ValueType;

class BooleanFieldHolder extends FieldHolder {

    private final RadioGroup radioGroup;

    BooleanFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.radioGroup = itemView.findViewById(R.id.radioGroup);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);

        if (fieldItem.getValue() != null && fieldItem.getValue().equals("true")) {
            radioGroup.check(R.id.optionYes);
        } else if (fieldItem.getValue() != null && fieldItem.getValue().equals("false")) {
            radioGroup.check(R.id.optionNo);
        } else {
            radioGroup.clearCheck();
        }

        radioGroup.setOnCheckedChangeListener(null);

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            String value;
            switch (i) {
                case R.id.optionYes:
                    value = "true";
                    break;
                case R.id.optionNo:
                    value = "false";
                    break;
                    default:
                        value = null;
                        break;
            }

            valueSavedListener.onValueSaved(fieldItem.getUid(), value);
        });
    }
}

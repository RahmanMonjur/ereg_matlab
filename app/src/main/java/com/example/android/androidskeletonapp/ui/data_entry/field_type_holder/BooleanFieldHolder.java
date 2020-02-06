package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.app.Application;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.hisp.dhis.android.core.common.ValueType;

class BooleanFieldHolder extends FieldHolder {

    private final RadioGroup radioGroup;
    GlobalClass globalVars;

    BooleanFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.radioGroup = itemView.findViewById(R.id.radioGroup);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        radioGroup.setOnCheckedChangeListener(null);

        globalVars = (GlobalClass) this.itemView.getContext().getApplicationContext();
        RadioButton yesButton = itemView.findViewById(R.id.optionYes);
        yesButton.setText(globalVars.getTranslatedWord(itemView.getContext().getResources().getString(R.string.yes)));
        RadioButton noButton = itemView.findViewById(R.id.optionNo);
        noButton.setText(globalVars.getTranslatedWord(itemView.getContext().getResources().getString(R.string.no)));

        if (fieldItem.getValue() != null && fieldItem.getValue().equals("true")) {
            radioGroup.check(R.id.optionYes);
        } else if (fieldItem.getValue() != null && fieldItem.getValue().equals("false")) {
            radioGroup.check(R.id.optionNo);
        } else {
            radioGroup.clearCheck();
        }

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

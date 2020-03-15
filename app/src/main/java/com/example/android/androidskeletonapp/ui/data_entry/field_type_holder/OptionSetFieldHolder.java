package com.example.android.androidskeletonapp.ui.data_entry.field_type_holder;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.android.androidskeletonapp.R;
import com.example.android.androidskeletonapp.data.Sdk;
import com.example.android.androidskeletonapp.data.service.forms.FormField;
import com.example.android.androidskeletonapp.ui.main.GlobalClass;

import org.hisp.dhis.android.core.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class OptionSetFieldHolder extends FieldHolder {

    private final Spinner spinner;
    private List<Option> optionList;
    private String fieldUid;
    private String fieldCurrentValue;
    GlobalClass globalVars;
    private boolean controlsEnable;

    OptionSetFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener, boolean controlsEnable) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
        this.controlsEnable = controlsEnable;
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        spinner.setOnItemSelectedListener(null);
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner(fieldItem.getOptionSetUid());

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner(String optionSetUid) {
        optionList = Sdk.d2().optionModule().options().byOptionSetUid().eq(optionSetUid).blockingGet();
        globalVars = (GlobalClass) this.itemView.getContext().getApplicationContext();
        List<String> optionListNames = new ArrayList<>();
        optionListNames.add(globalVars.getTranslatedWord("Please select from list"));
        //optionListNames.add(label.getText().toString());
        for (Option option : optionList) optionListNames.add(option.displayName());
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                R.layout.spinner_row, optionListNames));

        spinner.setOnItemSelectedListener(null);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (fieldCurrentValue == null || !Objects.equals(fieldCurrentValue, optionList.get(i - 1).code()))
                        valueSavedListener.onValueSaved(fieldUid, optionList.get(i - 1).code());
                } else if (fieldCurrentValue != null)
                    valueSavedListener.onValueSaved(fieldUid, null);

                TextView textView = (TextView) view;
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setEnabled(controlsEnable);
    }

    private void setInitialValue(String selectedCode) {
        for (int i = 0; i < optionList.size(); i++)
            if (Objects.equals(optionList.get(i).code(), selectedCode))
                spinner.setSelection(i + 1);
    }

}

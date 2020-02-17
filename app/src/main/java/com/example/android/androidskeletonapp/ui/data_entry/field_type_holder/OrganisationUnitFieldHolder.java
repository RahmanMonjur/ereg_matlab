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

import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrganisationUnitFieldHolder extends FieldHolder {

    private final Spinner spinner;
    private List<OrganisationUnit> optionList;
    private String fieldUid;
    private String fieldCurrentValue;
    GlobalClass globalVars;

    OrganisationUnitFieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView, valueSavedListener);
        this.spinner = itemView.findViewById(R.id.spinner);
    }

    void bind(FormField fieldItem) {
        super.bind(fieldItem);
        globalVars = (GlobalClass) itemView.getContext().getApplicationContext();
        fieldUid = fieldItem.getUid();
        fieldCurrentValue = fieldItem.getValue();

        setUpSpinner(fieldItem.getOptionSetUid());

        if (fieldCurrentValue != null)
            setInitialValue(fieldCurrentValue);
    }

    private void setUpSpinner(String optionSetUid) {
        optionList = Sdk.d2().organisationUnitModule().organisationUnits().byLevel().eq(5).blockingGet();


        List<String> optionListNames = new ArrayList<>();
        optionListNames.add(globalVars.getTranslatedWord("Please select from list"));
        //optionListNames.add(label.getText().toString());
        for (OrganisationUnit option : optionList) {
            if (!option.uid().equals("JoBQEuzxohv") || !option.uid().equals("NzTpSVrwBcE"))
                optionListNames.add(option.displayName());
        }
        spinner.setAdapter(new ArrayAdapter<>(itemView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, optionListNames));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    if (fieldCurrentValue == null || !Objects.equals(fieldCurrentValue, optionList.get(i - 1).uid()))
                        valueSavedListener.onValueSaved(fieldUid, optionList.get(i - 1).uid());
                } else if (fieldCurrentValue != null)
                    valueSavedListener.onValueSaved(fieldUid, null);

                TextView textView = (TextView) view;
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setInitialValue(String selectedCode) {
        for (int i = 0; i < optionList.size(); i++)
            if (Objects.equals(optionList.get(i).uid(), selectedCode))
                spinner.setSelection(i + 1);
    }

}
